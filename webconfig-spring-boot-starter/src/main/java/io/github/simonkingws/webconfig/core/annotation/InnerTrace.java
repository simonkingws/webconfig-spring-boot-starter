package io.github.simonkingws.webconfig.core.annotation;

import java.lang.annotation.*;

/**
 * 同一个服务内的方法追踪
 *
 * @author: ws
 * @date: 2024/2/27 9:09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface InnerTrace {

}
