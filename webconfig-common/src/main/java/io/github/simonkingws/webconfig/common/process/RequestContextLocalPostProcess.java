package io.github.simonkingws.webconfig.common.process;

import io.github.simonkingws.webconfig.common.context.RequestContextLocal;

/**
 * 拦截器设置上下后置处理
 *
 * @author: ws
 * @date: 2024/1/31 15:56
 */
public interface RequestContextLocalPostProcess {

    /**
     * 构建完参数全局请求参数的后置处理
     */
    default void afterRequestContextLocal(RequestContextLocal requestContextLocal){

    }

    /**
     * 全局请求参数被销毁后的处理
     */
    default void destroy(RequestContextLocal finalRequestContextLocal){

    }
}
