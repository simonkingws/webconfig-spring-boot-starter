package io.github.simonkingws.webconfig.core.aspect;

import io.github.simonkingws.webconfig.common.constant.SymbolConstant;
import io.github.simonkingws.webconfig.common.constant.TraceConstant;
import io.github.simonkingws.webconfig.common.context.RequestContextLocal;
import io.github.simonkingws.webconfig.common.context.TraceItem;
import io.github.simonkingws.webconfig.common.util.RequestHolder;
import io.github.simonkingws.webconfig.common.util.SpringContextHolder;
import io.github.simonkingws.webconfig.common.util.TraceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * <code>@InnerTrace</code>的切面
 *
 * @author: ws
 * @date: 2024/2/27 9:11
 */
@Slf4j
@Aspect
@Component
public class TraceAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(io.github.simonkingws.webconfig.core.annotation.InnerTrace)")
    public void pointcut(){

    }

    @Around("pointcut()")
    public Object Around(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestContextLocal local = RequestHolder.get();
        if (local == null || BooleanUtils.isNotTrue(local.getOpenTraceCollect()) || TraceContextHolder.isEmpty()) {
            return joinPoint.proceed();
        }

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String className = null;
        String methodName  = null;
        Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
        if (ArrayUtils.isNotEmpty(interfaces)) {
            for (Class<?> anInterface : interfaces) {
                Method[] methods = anInterface.getMethods();
                if (ArrayUtils.isNotEmpty(methods)) {
                    long count = Arrays.stream(methods).filter(m -> method.getName().equals(m.getName())).count();
                    if (count > 0) {
                        className = anInterface.getName();
                        methodName = method.getName() + SymbolConstant.BRACKET;
                        break;
                    }
                }
            }
        }
        if (StringUtils.isBlank(methodName)) {
            // 本地方法
            className = method.getDeclaringClass().getName();
            methodName = method.getName() + SymbolConstant.BRACKET;
        }
        String currentPos = String.format(TraceConstant.INVOKE_METHOND_NAME, className, methodName);
        log.info("@InnerTrace[{}]>>>>>>开始处理>>>>>>>>>>", currentPos);

        // 判断该方法是不是Rpc调用的：如果是就忽略，rpc会自己采集
        if (StringUtils.equals(currentPos, local.getRpcMethodName())) {
            return joinPoint.proceed();
        }

        // 采集来链路信息
        String applicationName = SpringContextHolder.getApplicationName();
        Integer traceSum = local.getTraceSum();
        traceSum++;
        try {
            Long size = stringRedisTemplate.opsForList().size(local.getTraceId());
            if (size != null) {
                traceSum += size.intValue();
            }
        }catch (Exception e) {
            log.warn("方法[{}]中的redis 配置未配置或配置异常：{}", currentPos, e.getMessage());
        }

        local.setEndPos(currentPos);
        local.setTraceSum(traceSum);

        TraceItem traceItem = TraceItem.copy2TraceItem(local);
        traceItem.setInvokeStartTime(Instant.now().toEpochMilli());
        traceItem.setConsumerApplicatName(applicationName);
        traceItem.setProviderApplicatName(applicationName);
        traceItem.setMethodName(methodName);
        traceItem.setClassName(className);

        List<TraceItem> traceItemList = TraceContextHolder.getTraceItems();
        Object proceed;
        try {
            // 执行方法
            proceed = joinPoint.proceed();

            traceItem.setInvokeEndTime(Instant.now().toEpochMilli());
            traceItemList.add(traceItem);
        }catch (Throwable th){
            // 有异常需要记录异常的链路信息，并将异常抛出
            try {
                long epochMilli = Instant.now().toEpochMilli();
                traceItem.setInvokeEndTime(epochMilli);
                traceItemList.add(traceItem);

                // 增肌异常信息的处理
                TraceItem exceptionTraceItem = TraceItem.builder().build();
                BeanUtils.copyProperties(traceItem, exceptionTraceItem);

                exceptionTraceItem.setInvokeStartTime(epochMilli);
                exceptionTraceItem.setClassName(TraceConstant.EXCEPTION_CLASS_NAME);
                exceptionTraceItem.setMethodName(TraceConstant.EXCEPTION_METHOD_NAME);
                exceptionTraceItem.setExecptionMsg(th.getMessage());
                traceItem.setInvokeEndTime(epochMilli);
                traceItemList.add(exceptionTraceItem);
            }catch (Exception e){
                log.warn("处理@InnerTrace注解异常：", e);
            }

            // 重新抛出异常不印象业务系统的捕获
            throw th;
        }

        return proceed;
    }
}
