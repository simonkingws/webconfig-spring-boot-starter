package io.github.simonkingws.webconfig.core.handler;

import io.github.simonkingws.webconfig.common.constant.TraceConstant;
import io.github.simonkingws.webconfig.common.context.RequestContextLocal;
import io.github.simonkingws.webconfig.common.context.TraceItem;
import io.github.simonkingws.webconfig.common.core.JsonResult;
import io.github.simonkingws.webconfig.common.exception.WebConfigException;
import io.github.simonkingws.webconfig.common.util.RequestHolder;
import io.github.simonkingws.webconfig.common.util.SpringContextHolder;
import io.github.simonkingws.webconfig.common.util.TraceContextHolder;
import io.github.simonkingws.webconfig.core.resolver.GlobalExceptionResponseResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author: ws
 * @date: 2024/1/29 11:04
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static String LOG_TEMPLATE = "请求路径[url]={}，[%s] 异常信息：";
    public static String REQUEST_PARAMS_EX_TEMPLATE = "参数[%s]：%s";
    public static String REQUEST_PARAMS_MISS_TEMPLATE = "请求参数缺失，缺失的参数：%s";

    @Autowired(required = false)
    private GlobalExceptionResponseResolver globalExceptionResponseResolver;

    /**
     * 校验参数绑定异常
     *
     * @author ws
     * @date 2024/1/29 11:13
     * @param e
     */
    @ExceptionHandler(BindException.class)
    public Object bindExceptionHandler(HttpServletRequest request, BindException e) {
        log.error(String.format(LOG_TEMPLATE, "bindExceptionHandler"), request.getRequestURL(), e);
        return doGenerateResult(e, getErrorMsgDefault(e.getBindingResult().getFieldErrors(), e.getMessage()));
    }

    /**
     * 方法参数校验异常处理
     *
     * @author ws
     * @date 2024/1/29 13:25
     * @param e
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object methodArgumentNotValidExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException e) {
        log.error(String.format(LOG_TEMPLATE, "methodArgumentNotValidExceptionHandler"), request.getRequestURL(), e);
        return doGenerateResult(e, getErrorMsgDefault(e.getBindingResult().getFieldErrors(), e.getMessage()));
    }

    /**
     * 参数校验配合@Requestparam的异常
     *
     * @author ws
     * @date 2024/3/7 16:29
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Object constraintViolationExceptionExceptionHandler(HttpServletRequest request, ConstraintViolationException e) {
        log.error(String.format(LOG_TEMPLATE, "constraintViolationExceptionExceptionHandler"), request.getRequestURL(), e);
        return doGenerateResult(e, getConstraintViolationExceptionMsg(e));
    }

    /**
     * 请求参数缺失异常
     *
     * @author ws
     * @date 2024/1/29 13:33
     * @param e
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object missingServletRequestParameterExceptionHandler(HttpServletRequest request, MissingServletRequestParameterException e) {
        log.error(String.format(LOG_TEMPLATE, "missingServletRequestParameterExceptionHandler"), request.getRequestURL(), e);
        return doGenerateResult(e, String.format(REQUEST_PARAMS_MISS_TEMPLATE, e.getParameterName()));
    }

    /**
     * 重复的请求拦截异常
     *
     * @author ws
     * @date 2024/1/30 16:51
     * @param request
     * @param e
     */
    @ExceptionHandler(WebConfigException.class)
    public Object requestLimitingInterceptorExceptionHandler(HttpServletRequest request, WebConfigException e) {
        log.error(String.format(LOG_TEMPLATE, "requestLimitingInterceptorExceptionHandler"), request.getRequestURL(), e);
        return doGenerateResult(e, e.getMessage());
    }

    /**
     * 兜底的异常处理
     *
     * @author ws
     * @date 2024/1/29 13:35
     * @param e
     */
    @ExceptionHandler(Exception.class)
    public Object exceptionHandler(HttpServletRequest request, Exception e) {
        log.error(String.format(LOG_TEMPLATE, "exceptionHandler"), request.getRequestURL(), e);
        return doGenerateResult(e, e.getMessage());
    }

    /**
     * 处理ConstraintViolationException的参数
     *
     * @author ws
     * @date 2024/3/7 16:34
     */
    private String getConstraintViolationExceptionMsg(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        if (CollectionUtils.isEmpty(violations)) {
            return e.getMessage();
        }

        return violations.stream().filter(Objects::nonNull)
                .map(cv -> String.format(REQUEST_PARAMS_EX_TEMPLATE, cv.getPropertyPath(), cv.getMessage()))
                .collect(Collectors.joining("\n"));
    }

    /**
     * 处理公用的参数校验异常的信息
     *
     * @author ws
     * @date 2024/1/31 10:45
     */
    private String getErrorMsgDefault(List<FieldError> fieldErrors, String detaultMsg) {
        String errorMsg = null;
        if (!CollectionUtils.isEmpty(fieldErrors)) {
            List<String> msgs = new ArrayList<>(fieldErrors.size());
            fieldErrors.forEach(error -> msgs.add(showErrorMsg(error)));
            errorMsg = String.join("\n", msgs);
        }
        return Optional.ofNullable(errorMsg).orElse(detaultMsg);
    }

    private String showErrorMsg(FieldError fieldError) {
        return String.format(REQUEST_PARAMS_EX_TEMPLATE, fieldError.getField(), fieldError.getDefaultMessage());
    }

    /**
     * 处理通用结果
     *
     * @author ws
     * @date 2024/1/31 10:43
     */
    private Object doGenerateResult(Exception e, String errorMsg) {
        disposeExceptionTrace(errorMsg);
        if (globalExceptionResponseResolver != null) {
            globalExceptionResponseResolver.pushExceptionNotice(e, errorMsg);
            return globalExceptionResponseResolver.resolveExceptionResponse(e, errorMsg);
        }
        return JsonResult.ofFail(errorMsg);
    }

    private void disposeExceptionTrace(String errorMsg) {
        try {
            // 保存链路信息到redis, 链路太长容易引起性能问题
            RequestContextLocal local = RequestHolder.get();
            if (local == null || (TraceContextHolder.isNotEmpty() && TraceContextHolder.hasException())) {
                return;
            }

            String applicationName = SpringContextHolder.getApplicationName();
            long exEpochMilli = Instant.now().toEpochMilli();

            TraceItem traceItem = TraceItem.copy2TraceItem(local);
            traceItem.setInvokeStartTime(exEpochMilli);
            traceItem.setConsumerApplicatName(applicationName);
            traceItem.setProviderApplicatName(applicationName);
            traceItem.setMethodName(TraceConstant.EXCEPTION_METHOD_NAME);
            traceItem.setClassName(TraceConstant.EXCEPTION_CLASS_NAME);
            traceItem.setExecptionMsg(errorMsg);
            traceItem.setInvokeEndTime(exEpochMilli);
            traceItem.setSpanEndMs(traceItem.getInvokeEndTime());

            TraceContextHolder.addTraceItem(traceItem);
        }catch (Exception e){
            log.warn("disposeExceptionTrace 异常：", e);
        }
    }
}
