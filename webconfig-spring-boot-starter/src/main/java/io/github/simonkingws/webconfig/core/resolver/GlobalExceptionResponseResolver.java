package io.github.simonkingws.webconfig.core.resolver;

/**
 * 全局响应实体配置
 *
 * @author: ws
 * @date: 2024/1/29 13:45
 */
public interface GlobalExceptionResponseResolver {

    /**
     * 自定义全局异常返回
     * 
     * @author ws
     * @date 2024/1/29 13:51
     * @param e 可以自定义处理异常或者自定义异常
     * @param errorMsg 已经处理好的异常信息
     */
    Object resolveExceptionResponse(Exception e, String errorMsg);

    /**
     * 推送异常通知
     *
     * @author ws
     * @date 2024/1/29 13:52
     * @param e
     * @param errorMsg 已经处理好的异常信息
     */
    default void pushExceptionNotice(Exception e, String errorMsg){
        
    }

}
