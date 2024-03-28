package io.github.simonkingws.webconfig.core.aspect;

import com.alibaba.fastjson2.JSON;
import io.github.simonkingws.webconfig.common.constant.SymbolConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 打印请求参数
 *
 * @author: ws
 * @date: 2024/3/18 9:50
 */
@Slf4j
@Aspect
@Component
public class PrintParamsAspect {

    @Pointcut("@annotation(io.github.simonkingws.webconfig.core.annotation.PrintParams)")
    public void pointcut(){

    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            // 拼接本地方法名
            String methodName = method.getDeclaringClass().getName() + SymbolConstant.DOT + method.getName() + SymbolConstant.BRACKET;

            log.info("方法【{}】，请求参数:\r\n{}",methodName, JSON.toJSONString(request.getParameterMap()));
        }
    }
}
