package io.github.simonkingws.webconfig.core.aspect;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.github.simonkingws.webconfig.common.constant.SymbolConstant;
import io.github.simonkingws.webconfig.common.core.WebconfigProperies;
import io.github.simonkingws.webconfig.common.exception.WebConfigException;
import io.github.simonkingws.webconfig.core.annotation.RequestLimiting;
import io.github.simonkingws.webconfig.core.contant.AnnotationConstant;
import io.github.simonkingws.webconfig.core.resolver.CacheResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 注解 &#64;RequestLimiting的切面
 *
 * @author: ws
 * @date: 2024/1/30 10:57
 */
@Slf4j
@Aspect
@Component
public class RequestLimitingAspect {

    @Autowired
    private WebconfigProperies webconfigProperies;
    @Autowired
    private Map<String, CacheResolver> cacheResolverMap;

    /**
     * 切面
     *
     * @author ws
     * @date 2024/1/31 10:47
     */
    @Pointcut("@annotation(io.github.simonkingws.webconfig.core.annotation.RequestLimiting)")
    public void pointcut(){

    }

    /**
     * 环绕通知
     *
     * @author ws
     * @date 2024/1/31 10:47
     */
    @Around("pointcut()")
    public Object Around(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        log.info("@RequestLimiting注解拦截方法[{}]>>>>>>开始处理>>>>>>>>>>", method.getName());
        RequestLimiting[] annotations = method.getAnnotationsByType(RequestLimiting.class);
        // 多个@RequestLimiting， 取其中一个
        RequestLimiting requestLimiting = annotations[0];

        CacheResolver cacheResolver = cacheResolverMap.get(webconfigProperies.getRequestLimitCacheMode());
        String requestToken = null;
        boolean cleanToken = true;
        try {
            requestToken = getRequestToken(requestLimiting, request);
            log.info("重复请求请求参数：requestToken={}", requestToken);

            int expiredSec = requestLimiting.expiredSec();
            if (expiredSec == 0) {
                expiredSec = requestLimiting.maxExpiredSec();
            }
            // 向缓存在设置值
            Boolean ifPresent = cacheResolver.setIfAbsent(requestToken, requestToken, expiredSec, TimeUnit.SECONDS);
            if (BooleanUtils.isTrue(ifPresent)) {
                 return joinPoint.proceed();
            }

            cleanToken = false;
            // 没有设置成功
            assert response != null;
            switch (requestLimiting.returnPolicy()) {
                case IGNORE:
                    log.info(">>>>>>重复请求被忽略了...");
                    break;
                case RETURN_JSON :
                    log.info(">>>>>>重复请返回提示信息...");
                    throw new WebConfigException(requestLimiting.msg());
                case RETURN_PAGE:
                    String redirect = requestLimiting.redirect();
                    log.info(">>>>>>重复请求返回页面：{}", redirect);
                    response.sendRedirect(request.getContextPath()+redirect+"?backUri="+request.getRequestURI());
                    break;
            }
        } catch (Throwable e) {
            throw new WebConfigException(e.getMessage());
        } finally {
            if (cleanToken) {
                // 请求结束胡取消限制
                cacheResolver.remove(requestToken);
            }
            log.info("@RequestLimiting注解拦截方法[{}]>>>>>>>>>>>执行结束>>>>>>>>>", method.getName());
        }
        return null;
    }

    /**
     * 后置处理
     *
     * @author ws
     * @date 2024/1/31 10:47
     */
    @After("pointcut()")
    public void after(JoinPoint joinPoint) throws Exception {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RequestLimiting[] annotations = method.getAnnotationsByType(RequestLimiting.class);
        // 多个@RequestLimiting， 取其中一个
        RequestLimiting requestLimiting = annotations[0];
        Class<?> clazz = requestLimiting.afterCallback();
        String callbackMethod = clazz.getName();
        if (!AnnotationConstant.METHOD_NAME_VOID.equalsIgnoreCase(callbackMethod)) {
            ReflectionUtils.invokeMethod(clazz.getMethod(AnnotationConstant.CLAZZ_METHOD_NAME, clazz), Void.class);
        }
    }

    private String getRequestToken(RequestLimiting requestLimiting, HttpServletRequest request) throws Exception {
        // 结果集
        TreeMap<String, Object> resultMap = new TreeMap<>(String::compareTo);

        String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        // 文本数据需要处理
        if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)
                || MediaType.APPLICATION_JSON_UTF8_VALUE.equalsIgnoreCase(contentType)) {

            String jsonStr = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(jsonStr)) {
                JSONObject jsonObject = JSON.parseObject(jsonStr);
                if (jsonObject != null) {
                    resultMap.putAll(jsonObject);
                }
            }
        }else{
            // 表单提交
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (!CollectionUtils.isEmpty(parameterMap)) {
                parameterMap.forEach((k,v) -> resultMap.put(k, String.join(SymbolConstant.COMMA, v)));
            }
        }

        // 过滤有效字段
        String[] incluedParams = requestLimiting.incluedParams();
        if (ArrayUtils.isNotEmpty(incluedParams)) {
            Map<String, Object> collect = resultMap.entrySet().stream()
                    .filter(entry -> ArrayUtils.contains(incluedParams, entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            resultMap.clear();
            resultMap.putAll(collect);
        }
        // 剔除无关字段
        String[] ignoreParams = requestLimiting.ignoreParams();
        if (ArrayUtils.isNotEmpty(ignoreParams)) {
            Arrays.stream(ignoreParams).forEach(resultMap::remove);
        }

        // 封装其他参数
        resultMap.put("uri", request.getRequestURI());
        resultMap.put("customize", requestLimiting.customize());
        resultMap.put("token", request.getHeader("token"));
        resultMap.put("userAgent", request.getHeader("User-Agent"));
        resultMap.put("deviceNumber", request.getHeader("device-number"));
        resultMap.put("ip", getIpAddr(request));

        StringBuilder sb = new StringBuilder();
        resultMap.forEach((k, v) -> sb.append(k).append(SymbolConstant.EQUALS).append(v).append(SymbolConstant.CONNECTION));
        log.info("@RequestLimiting 处理的有效请求参数>>>>>：{}", sb);

        return DigestUtils.md5DigestAsHex(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(!StringUtils.isEmpty(ip) && ip.length() > 15) {
            if(ip.indexOf(SymbolConstant.COMMA) > 0) {
                ip = ip.substring(0, ip.indexOf(SymbolConstant.COMMA));
            }
        }
        return ip;
    }
}
