package com.simonkingws.application.demos.web;

import com.alibaba.fastjson2.util.DateUtils;
import com.simonkingws.api.service.BookService1FeignClient;
import io.github.simonkingws.webconfig.common.constant.DateFormatConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 测试Feign接口
 *
 * @author: ws
 * @date: 2024/2/5 17:16
 */
@Slf4j
@RestController
@RequestMapping("/feign")
public class TestFeignController {

    @Autowired
    private BookService1FeignClient bookService1FeignClient;

    /**
     * 测试Fiegn
     *
     * @author ws
     * @date 2024/2/5 16:11
     * @param
     * @return {@link String}
     */
    @RequestMapping("/testFeignClient")
    public String testFeignClient()  {
        return bookService1FeignClient.bookName(2);
    }

    /**
     * 测试Feign接口时间参数的时差问题
     *
     * @author ws
     * @date 2024/2/20 15:43
     */
    @RequestMapping("/testFeignDate")
    public String testFeignDate()  {
        Date date = new Date();
        log.info("date:{}", DateUtils.format(date));
        log.info("bookService1FeignClient date:{}", bookService1FeignClient.checkDate(date));
        return bookService1FeignClient.checkDate(date);
    }

    /**
     * 测试Feign接口时间参数的时差问题：兼容jdk8
     *
     * @author ws
     * @date 2024/2/20 16:44
     */
    @RequestMapping("/testFeignLocalDateTime")
    public String testFeignLocalDateTime()  {
        LocalDateTime localDateTime = LocalDateTime.now();
        log.info("localDateTime:{}", localDateTime.format(DateTimeFormatter.ofPattern(DateFormatConstant.STANDARD_DATE_TIME)));
        log.info("bookService1FeignClient date:{}", bookService1FeignClient.checkLocalDateTime(localDateTime));
        return bookService1FeignClient.checkLocalDateTime(localDateTime);
    }
}
