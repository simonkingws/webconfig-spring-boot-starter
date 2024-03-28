package com.simonkingws.application;

import io.github.simonkingws.webconfig.core.filter.ParameterTrimFilter;
import io.github.simonkingws.webconfig.core.filter.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 *
 * @author: ws
 * @date: 2024/2/2 10:44
 */
@Configuration
public class BeanConfig {

    @Bean
    public FilterRegistrationBean<XssFilter> xssFiler(){
        FilterRegistrationBean<XssFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XssFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<ParameterTrimFilter> parameterTrimFilter(){
        FilterRegistrationBean<ParameterTrimFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ParameterTrimFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
