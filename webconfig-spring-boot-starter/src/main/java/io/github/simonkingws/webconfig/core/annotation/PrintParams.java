package io.github.simonkingws.webconfig.core.annotation;

import java.lang.annotation.*;

/**
 * 打印请求参数
 *
 * @author: ws
 * @date: 2024/2/27 9:09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PrintParams {

}
