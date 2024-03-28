package io.github.simonkingws.webconfig.common.constant;

/**
 * 调用链路常量
 *
 * @author: ws
 * @date: 2024/2/20 14:43
 */
public interface TraceConstant {

    /** 调用的方法名称 */
    String INVOKE_METHOND_NAME = "%s.%s()";
    /** 链路追踪的名称: [序号][消费端服务名称][提供端服务名称][方法名称] */
    String INVOKE_TRACE = "([%d][%s][%s][%s])";

    String EXCEPTION_CLASS_NAME = "java.lang.Throwable";
    String EXCEPTION_METHOD_NAME = "try-catch()";

    String COLLECT_PATH = "%s://%s/trace/collect";

    String JSON_CONTENT_TYPE = "application/json;charset=utf-8";
}
