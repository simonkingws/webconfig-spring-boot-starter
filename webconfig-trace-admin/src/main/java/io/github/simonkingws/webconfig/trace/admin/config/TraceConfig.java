package io.github.simonkingws.webconfig.trace.admin.config;

import io.github.simonkingws.webconfig.trace.admin.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 配置类
 *
 * @author: ws
 * @date: 2024/3/5 17:52
 */
@Slf4j
@Configuration
public class TraceConfig implements WebMvcConfigurer {

    /**
     * 自定义线程池
     *
     * @author ws
     * @date 2024/3/5 17:36
     */
    @Bean("customThreadPoolExecutor")
    public Executor customThreadPoolExecutor(){
        int processNum = Runtime.getRuntime().availableProcessors();
        log.info("可用的处理器数量：{}", processNum);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(processNum + 1);
        executor.setMaxPoolSize(2 * processNum);
        // 大约一个任务估算5K，内存中限制10M。对列大小就是2048
        executor.setQueueCapacity(2048);
        executor.setThreadNamePrefix("customTaskExecutor-");
        // 线程池关闭之前等待其他任务执行完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 超过等待时间（10分钟）直接销毁
        executor.setAwaitTerminationSeconds(10 * 60);
        // 如果队列满了，丢弃队列中最老的任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/css/**","/js/**","/image/**")
                .excludePathPatterns("/login","/login/**")
                .excludePathPatterns("/trace/**");
    }
}
