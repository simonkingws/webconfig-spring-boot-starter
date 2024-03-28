package io.github.simonkingws.webconfig.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 全局Spring上下文
 *
 * @author: ws
 * @date: 2024/2/5 8:54
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class SpringContextHolder implements ApplicationContextAware, EnvironmentAware {

    private static ApplicationContext applicationContext;
    private static Environment environment;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (this.applicationContext != null) {
            log.info("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为: {}", this.applicationContext);
        }
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        if (this.environment != null) {
            log.info("SpringContextHolder中的Environment被覆盖, 原有Environment为:{}", this.environment);
        }
        this.environment = environment;
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static Object getBean(String name, Class requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    public static String getPropertyName(String propertyName) {
        return environment.getProperty(propertyName);
    }

    public static String getApplicationName() {
        return environment.getProperty("spring.application.name");
    }
}
