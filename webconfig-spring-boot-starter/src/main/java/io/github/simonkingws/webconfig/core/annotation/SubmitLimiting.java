package io.github.simonkingws.webconfig.core.annotation;

import io.github.simonkingws.webconfig.core.contant.RunMode;

import java.lang.annotation.*;

/**
 * 表单提交的限制（重复请求）
 *
 * @author ws
 * @date 2024/3/4 14:27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SubmitLimiting {

    /**
     * 运行模式：
     * RunMode.INIT :生成页面的唯一标识
     * RunMode.VALID :验证请求是否重复提交
     */
    RunMode mode() default RunMode.VALID;

}
