package io.github.simonkingws.webconfig.core.aspect;

import io.github.simonkingws.webconfig.common.constant.RedisConstant;
import io.github.simonkingws.webconfig.common.constant.RequestHeaderConstant;
import io.github.simonkingws.webconfig.common.core.WebconfigProperies;
import io.github.simonkingws.webconfig.common.exception.WebConfigException;
import io.github.simonkingws.webconfig.core.annotation.SubmitLimiting;
import io.github.simonkingws.webconfig.core.contant.RunMode;
import io.github.simonkingws.webconfig.core.resolver.CacheResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @SubmitLimiting 注解的切面
 *
 * @author: ws
 * @date: 2024/3/4 14:36
 */
@Slf4j
@Aspect
@Component
public class SubmitLimitingAspect {

    @Autowired
    private WebconfigProperies webconfigProperies;
    @Autowired
    private Map<String, CacheResolver> cacheResolverMap;

    @Pointcut("@annotation(io.github.simonkingws.webconfig.core.annotation.SubmitLimiting)")
    public void pointcut(){

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;

        CacheResolver cacheResolver = cacheResolverMap.get(webconfigProperies.getRequestLimitCacheMode());

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        log.info("@SubmitLimiting注解拦截方法[{}()]>>>>>>开始处理>>>>>>>>>>", method.getName());
        SubmitLimiting[] annotations = method.getAnnotationsByType(SubmitLimiting.class);
        // 多个@RequestLimiting， 取其中一个
        SubmitLimiting submitLimiting = annotations[0];
        if (submitLimiting.mode().equals(RunMode.INIT)) {
            HttpServletResponse response = requestAttributes.getResponse();
            assert response != null;
            response.setHeader(RequestHeaderConstant.ONCE_ACCESS_TOKEN, UUID.randomUUID().toString());
        }else if (submitLimiting.mode().equals(RunMode.VALID)) {
            // 验证是否已经访问过
            HttpServletRequest request = requestAttributes.getRequest();
            String header = request.getHeader(RequestHeaderConstant.ONCE_ACCESS_TOKEN);
            if (StringUtils.isBlank(header)) {
                throw new WebConfigException("请求头中没有包含校验重复提交的参数！");
            }
            String cacheKey = String.format(RedisConstant.CACHE_KEY, header);
            Boolean ifAbsent = cacheResolver.setIfAbsent(cacheKey, method.getName(), 30, TimeUnit.MINUTES);
            if (!ifAbsent) {
                throw new WebConfigException("该请求已提交，请勿重复操作！");
            }
        }

        return joinPoint.proceed();
    }

    @AfterThrowing(value = "pointcut()")
    public void afterThrowing()  {
        // 方法内部出现异常，需要清除缓存
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();

            // 清除缓存，允许重复执行
            String cacheKey = String.format(RedisConstant.CACHE_KEY, request.getHeader(RequestHeaderConstant.ONCE_ACCESS_TOKEN));
            CacheResolver cacheResolver = cacheResolverMap.get(webconfigProperies.getRequestLimitCacheMode());
            cacheResolver.remove(cacheKey);
        }
    }
}
