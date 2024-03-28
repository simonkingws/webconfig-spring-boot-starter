package io.github.simonkingws.webconfig.common.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Webconfig 项目的配置信息
 *
 * @author: ws
 * @date: 2024/1/30 18:58
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "springboot.webconfig")
public class WebconfigProperies {

    /**
     * 重复请求的缓存模式
     */
    private String requestLimitCacheMode = "redis";

    /**
     * 是否开启检验参数完成后返回所有错误信息，默认一个参数异常就返回
     */
    private Boolean validFailFast = true;

    /**
     * 是否开启全局请求参数拦截
     */
    private Boolean requestContextInterceptor = true;

    /**
     * 全局请求参数拦截路径
     */
    private String interceptorPathPatterns = "/**";

    /**
     * 全局请求参数忽略路径
     */
    private String excludePathPatterns;

    /**
     * 是否开启dubbo全局请求参数拦截
     */
    private Boolean dubboInterceptor = true;

    /**
     * 是否开启Feign全局请求头参数拦截
     */
    private Boolean feignInterceptor = true;

    /**
     * 是否开启链路信息采集
     */
    private Boolean openTraceCollect = false;

    /**
     * 链路信息采集的地址：IP/域名:PORT
     */
    private String traceCollectAddress;

    /**
     * 请求的协议，默认http
     */
    private String protocol = "http";

    /**
     * arthas 是否开启,默认关闭。关闭状态可以使用arthas的原生配置
     */
    private Boolean arthasOpen = false;

    /**
     * arthas 是否开启,默认关闭。关闭状态可以使用arthas的原生配置
     */
    private Boolean arthasIsSelfConfig = false;

    /**
     * arthas ip
     */
    private String arthasIp = "0.0.0.0";

    /**
     * arthas 服务唯一标识：默认是为空，系统自动生成。
     * <pre>
     *     通过 http://localhost:8080/apps.html 可以查看
     * </pre>
     */
    private String arthasAgentId;

    /**
     * arthas 服务地址
     */
    private String arthasTunnelServer;

}
