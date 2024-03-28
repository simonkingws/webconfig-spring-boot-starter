package io.github.simonkingws.webconfig.feign.config;

import io.github.simonkingws.webconfig.common.constant.DateFormatConstant;
import io.github.simonkingws.webconfig.common.formatter.LocalDateTimeFormatter;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;

/**
 * 日期依@requestParam传递时存在时差问题，解决方案统一按照字符串传递
 *
 * @author: ws
 * @date: 2024/1/31 17:13
 */
@Configuration
public class FeignDateFormatRegister implements FeignFormatterRegistrar {
    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addFormatter(new DateFormatter(DateFormatConstant.STANDARD_DATE_TIME));
        registry.addFormatter(new LocalDateTimeFormatter(DateFormatConstant.STANDARD_DATE_TIME));
    }
}
