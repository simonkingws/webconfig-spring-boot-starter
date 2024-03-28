package io.github.simonkingws.webconfig.core.annotation;

import io.github.simonkingws.webconfig.core.aspect.AfterCallback;
import io.github.simonkingws.webconfig.core.contant.Policy;

import java.lang.annotation.*;

/**
 * <pre>
 * 请求频次控制:
 * <Strong>@RequestLimiting</Strong> 注解解决上一次请求未完成情况下，重复请求的问题
 * </pre>
 *
 * eg1: 基本使用，返回JSON
 * <pre>
 * &#64;GetMapping( "/test01")
 * &#64;RequestLimiting()
 * public String test01() {
 *      return "welcome";
 * }
 * </pre>
 *
 * eg2：自定义参数
 * <pre>
 * &#64;GetMapping( "/test02")
 * &#64;RequestLimiting(customize = "it is test", ignoreParams = {"a"}, incluedParams = {"b"})
 * public String test02() {
 *      return "welcome";
 * }
 * </pre>
 *
 * eg3：定义返回页面
 * <pre>
 * &#64;GetMapping( "/test03")
 * &#64;RequestLimiting(mode = ReturnMode.RETURN_PAGE)
 * public String test03() {
 *      return "welcome";
 * }
 * </pre>
 *
 * @author: ws
 * @date: 2024/1/29 18:38
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RequestLimiting {

    /**
     * 返回类型
     */
    Policy returnPolicy() default Policy.IGNORE;

    /**
     * 重复提交的数据返回信息提示
     */
    String msg() default "数据正在处理中，请勿重复请求!";


    /**
     * 自定义重复提交跳转的页面
     */
    String redirect() default "/html/302.html";

    /**
     * 自定义信息
     */
    String customize() default "";

    /**
     * 忽略请求参数或者字段
     */
    String[] ignoreParams() default {};

    /**
     * 有效的请求参数： 如果此参数不为空，则直接使用该请求参数，不会自动获取所有的请求参数
     */
    String[] incluedParams() default {};

    /**
     * 过期时间：默认0表示请求返回之后，解除限制。自定义之后按照设置的时间限制
     */
    int expiredSec() default 0;

    /**
     * 最大过期时间：默认50s（50s请求未返回，取消限制，防止nginx等反向代理的轮询请求）
     */
    int maxExpiredSec() default 50;

    /**
     * 方法执行完之后的回调，方法必须继承{@link AfterCallback}.
     */
    Class<?> afterCallback() default void.class;

}
