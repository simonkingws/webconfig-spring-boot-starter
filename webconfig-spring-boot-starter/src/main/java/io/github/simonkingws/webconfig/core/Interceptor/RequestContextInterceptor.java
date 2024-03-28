package io.github.simonkingws.webconfig.core.Interceptor;

import io.github.simonkingws.webconfig.common.constant.MDCKey;
import io.github.simonkingws.webconfig.common.constant.RequestHeaderConstant;
import io.github.simonkingws.webconfig.common.constant.SymbolConstant;
import io.github.simonkingws.webconfig.common.constant.TraceConstant;
import io.github.simonkingws.webconfig.common.context.RequestContextLocal;
import io.github.simonkingws.webconfig.common.context.TraceItem;
import io.github.simonkingws.webconfig.common.core.WebconfigProperies;
import io.github.simonkingws.webconfig.common.process.RequestContextLocalPostProcess;
import io.github.simonkingws.webconfig.common.util.RequestHolder;
import io.github.simonkingws.webconfig.common.util.TraceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Optional;

/**
 * 全局参数拦截器
 *
 * @author: ws
 * @date: 2024/1/31 16:12
 */
@Slf4j
@Component
public class RequestContextInterceptor implements HandlerInterceptor {

    @Value("${spring.application.name:}")
    private String applicationName;

    @Autowired(required = false)
    private RequestContextLocalPostProcess requestContextLocalPostProcess;
    @Autowired
    private WebconfigProperies webconfigProperies;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!webconfigProperies.getRequestContextInterceptor()) {
            return false;
        }

        RequestContextLocal requestContextLocal = RequestContextLocal.buildContext(request);
        // 记录方法的全限定类型和方法
        String requstUrl = request.getRequestURL().toString();
        String endPos = requstUrl;
        String className = null;
        String methodName = null;
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            className = handlerMethod.getBeanType().getName();
            methodName = handlerMethod.getMethod().getName();
            endPos = String.format(TraceConstant.INVOKE_METHOND_NAME, handlerMethod.getBeanType().getName(), handlerMethod.getMethod().getName());
        }
        if (StringUtils.isBlank(requestContextLocal.getStartPos())) {
            requestContextLocal.setStartPos(endPos);
        }
        requestContextLocal.setEndPos(endPos);
        requestContextLocal.setRequestUrl(requstUrl);

        if (isRpcInvoke(request)) {
            // 采集链路信息
            collectTraceData(request, requestContextLocal);
        }else{
            requestContextLocal.setOpenTraceCollect(webconfigProperies.getOpenTraceCollect());
            if (requestContextLocalPostProcess != null) {
                requestContextLocalPostProcess.afterRequestContextLocal(requestContextLocal);
            }

            // 收集入口的链路数据
            if (BooleanUtils.isTrue(webconfigProperies.getOpenTraceCollect())) {
                TraceItem traceItem = getTraceItemByRequestContextLocal(requestContextLocal, className, methodName);
                traceItem.setOrder(0);
                traceItem.setInvokeStartTime(traceItem.getSpanId());
                traceItem.setConsumerApplicatName(applicationName);

                TraceContextHolder.addTraceItem(traceItem);
            }
        }

        RequestHolder.add(requestContextLocal);
        MDC.put(MDCKey.TRACEID, requestContextLocal.getTraceId());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (isRpcInvoke(request)) {
            traceDataSave();
        }else {
            RequestHolder.remove(requestContextLocalPostProcess);
        }
    }

    private boolean isRpcInvoke(HttpServletRequest request) {
        String feignMark = request.getHeader(RequestHeaderConstant.FIEGN_MARK_KEY);
        return StringUtils.isNotBlank(feignMark);
    }

    private void traceDataSave() {
        // 保存链路信息到redis, 链路太长容易引起性能问题
        RequestContextLocal local = RequestHolder.get();
        if (Optional.ofNullable(local).map(RequestContextLocal::getOpenTraceCollect).orElse(false)) {
            try {
                // 补充首次进入链路信息的结束时间
                if (TraceContextHolder.isNotEmpty()) {
                    TraceItem traceItem = TraceContextHolder.getTraceItems().get(0);
                    traceItem.setInvokeEndTime(Instant.now().toEpochMilli());
                    traceItem.setSpanEndMs(traceItem.getInvokeEndTime());

                    // 内部方法调用需要补全spanEndMs
                    TraceContextHolder.getTraceItems().stream().filter(item -> item.getSpanEndMs() == null).forEach(item -> {
                        item.setSpanEndMs(traceItem.getSpanEndMs());
                    });

                    stringRedisTemplate.opsForList().rightPushAll(local.getTraceId(), TraceContextHolder.toStringList());
                }
            }catch (Exception e){
                log.warn("redis未配置或reids服务没有启动：{}", e.getMessage());
            }
        }

        RequestHolder.remove();
    }

    private void collectTraceData(HttpServletRequest request, RequestContextLocal requestContextLocal) {
        if (BooleanUtils.isTrue(requestContextLocal.getOpenTraceCollect())) {
            log.info(">>>>>Feign调用-链路信息采集启用中......");
            String feignMethodName = request.getHeader(RequestHeaderConstant.FIEGN_METHOD_NAME);
            String feignClassName = request.getHeader(RequestHeaderConstant.FIEGN_CLASS_NAME);
            String consumerApplicationName = request.getHeader(RequestHeaderConstant.FIEGN_CONSUMER_APPLICATION_NAME);

            TraceItem traceItem = getTraceItemByRequestContextLocal(requestContextLocal, feignClassName, feignMethodName);
            traceItem.setInvokeStartTime(Instant.now().toEpochMilli());
            traceItem.setConsumerApplicatName(consumerApplicationName);

            // 链路信息保存在线程中
            TraceContextHolder.addTraceItem(traceItem);
            // 上下文设置远程调用的方法
            requestContextLocal.setRpcMethodName(String.format(TraceConstant.INVOKE_METHOND_NAME, feignClassName, feignMethodName));
        }else{
            log.info(">>>>>>Feign调用-链路信息采集已被禁用中......");
        }
    }

    private TraceItem getTraceItemByRequestContextLocal(RequestContextLocal requestContextLocal, String className, String methodName) {
        TraceItem traceItem = TraceItem.copy2TraceItem(requestContextLocal);
        traceItem.setProviderApplicatName(applicationName);
        traceItem.setMethodName(methodName + SymbolConstant.BRACKET);
        traceItem.setClassName(className);
        return traceItem;
    }
}
