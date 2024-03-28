package io.github.simonkingws.webconfig.core.handler;

import io.github.simonkingws.webconfig.common.constant.DateFormatConstant;
import io.github.simonkingws.webconfig.common.formatter.LocalDateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 全局日期的处理
 *
 * @author: ws
 * @date: 2024/1/29 14:11
 */
@Slf4j
@ControllerAdvice
public class GlobalWebHandler {

    /**
     * 处理web的data数据，这里主要处理form表单的日期
     *
     * @author ws
     * @date 2024/1/29 14:13
     * @param binder
     */
    @InitBinder
    public void globalWebDataBinder(WebDataBinder binder){
        DateFormatter dateFormatter = new DateFormatter(DateFormatConstant.STANDARD_DATE_TIME);
        dateFormatter.setFallbackPatterns(DateFormatConstant.FORMAT_PATTERNS);
        binder.addCustomFormatter(dateFormatter, Date.class);

        //兼容jdk8 LocalDateTime
        binder.addCustomFormatter(new LocalDateTimeFormatter(), LocalDateTime.class);
    }
}
