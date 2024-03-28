package io.github.simonkingws.webconfig.dubbo3.filter;

import io.github.simonkingws.webconfig.common.constant.SymbolConstant;
import io.github.simonkingws.webconfig.common.constant.TraceConstant;
import io.github.simonkingws.webconfig.common.context.RequestContextLocal;
import io.github.simonkingws.webconfig.common.context.TraceItem;
import io.github.simonkingws.webconfig.common.util.RequestHolder;
import io.github.simonkingws.webconfig.common.util.SpringContextHolder;
import io.github.simonkingws.webconfig.common.util.TraceContextHolder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 全局Filter
 *
 * @author: ws
 * @date: 2023/5/17 9:51
 */
@Slf4j
@Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER})
public class DubboRpcFilter implements Filter, BaseFilter.Listener {

    // 共享的dubbo全局key
    public static final String RPC_CONTEXT_KEY = "dubbo3:rpc:request:context";

    @Setter
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 获取本地信息
        RpcServiceContext rpcContext = RpcContext.getServiceContext();
        String className = rpcContext.getUrl().getServiceKey();
        String methodName = rpcContext.getMethodName();
        String currentPos = String.format(TraceConstant.INVOKE_METHOND_NAME, className, methodName);
        if (rpcContext.isConsumerSide()) {
            // 消费端： 同步本地信息（写入dubbo的上下文中）
            log.info(">>>>>>>消费端：进入DubboRpcFilter，拦截[{}]方法，将本地上下文保存在dubbo中", currentPos);
            RpcContextAttachment clientAttachment = RpcContext.getClientAttachment();
            clientAttachment.setAttachment(RPC_CONTEXT_KEY,  RequestHolder.get());
        }
        if (rpcContext.isProviderSide()) {
            // 提供端：增加链路信息
            RpcContextAttachment serverAttachment = RpcContext.getServerAttachment();
            Object objectAttachment = serverAttachment.getObjectAttachment(RPC_CONTEXT_KEY);
            if (objectAttachment != null) {
                RequestContextLocal local = (RequestContextLocal) objectAttachment;
                log.info(">>>>>>>提供端：进入DubboRpcFilter，拦截[{}]方法被调用，将dubbo上下文的数据保存到本地上下文", currentPos);

                Integer traceSum = Optional.ofNullable(local.getTraceSum()).orElse(0);
                traceSum++;

                local.setTraceSum(traceSum);
                local.setEndPos(currentPos);
                local.setRpcMethodName(currentPos);
                local.setSpanId(Instant.now().toEpochMilli());

                // 将处理好的数据放进本地上下文
                RequestHolder.add(local);

                // 封装链路信息
                if (BooleanUtils.isTrue(local.getOpenTraceCollect())) {
                    log.info(">>>>>Dubbo调用-链路信息采集启用中......");
                    TraceItem traceItem = TraceItem.copy2TraceItem(local);
                    traceItem.setSpanId(Instant.now().toEpochMilli());
                    traceItem.setInvokeStartTime(traceItem.getSpanId());
                    traceItem.setMethodName(methodName + SymbolConstant.BRACKET);
                    traceItem.setClassName(className);
                    traceItem.setConsumerApplicatName(serverAttachment.getRemoteApplicationName());
                    traceItem.setProviderApplicatName(SpringContextHolder.getApplicationName());

                    TraceContextHolder.addTraceItem(traceItem);
                }else{
                    log.info(">>>>>>Dubbo调用-链路信息采集已被禁用中......");
                }
            }
        }

        return invoker.invoke(invocation);
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        // 获取本地信息
        RpcServiceContext rpcContext = RpcContext.getServiceContext();
        RpcContextAttachment serverAttachment = RpcContext.getServerAttachment();
        if (rpcContext.isProviderSide()) {
            try {
                if (TraceContextHolder.isNotEmpty()) {
                    List<TraceItem> traceItems = TraceContextHolder.getTraceItems();
                    TraceItem traceItem = traceItems.get(0);
                    String traceId = traceItem.getTraceId();
                    traceItem.setInvokeEndTime(Instant.now().toEpochMilli());
                    traceItem.setSpanEndMs(traceItem.getInvokeEndTime());

                    // 内部方法调用需要补全spanEndMs
                    traceItems.stream().filter(item -> item.getSpanEndMs() == null).forEach(item -> {
                        item.setSpanEndMs(traceItem.getSpanEndMs());
                    });

                    // 如果链路中没有异常的链路，则将dubbo的链路异常加入
                    if (appResponse.hasException() && !TraceContextHolder.hasException()) {
                        // 创建异常链路
                        TraceItem exTraceItem = TraceItem.builder().build();
                        BeanUtils.copyProperties(traceItem, exTraceItem);
                        long exEpochMilli = Instant.now().toEpochMilli();

                        exTraceItem.setInvokeStartTime(exEpochMilli);
                        exTraceItem.setMethodName(TraceConstant.EXCEPTION_METHOD_NAME);
                        exTraceItem.setClassName(TraceConstant.EXCEPTION_CLASS_NAME);
                        exTraceItem.setExecptionMsg(appResponse.getException().getMessage());
                        exTraceItem.setConsumerApplicatName(serverAttachment.getRemoteApplicationName());
                        exTraceItem.setProviderApplicatName(SpringContextHolder.getApplicationName());
                        exTraceItem.setInvokeEndTime(exEpochMilli);
                        exTraceItem.setSpanEndMs(exTraceItem.getInvokeEndTime());
                        traceItems.add(exTraceItem);
                    }

                    try {
                        // 保存链路信息到redis, 链路太长容易引起性能问题
                        if (stringRedisTemplate != null) {
                            stringRedisTemplate.opsForList().rightPushAll(traceId, TraceContextHolder.toStringList());
                        }
                    }catch (Exception e){
                        log.warn("redis未配置或reids服务没有启动：{}", e.getMessage());
                    }
                }
            }finally {
                serverAttachment.clearAttachments();
                // 调用结束之后需要删除本地线程的数据，防止OOM
                RequestHolder.remove();
            }
        }
    }

    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {

    }
}