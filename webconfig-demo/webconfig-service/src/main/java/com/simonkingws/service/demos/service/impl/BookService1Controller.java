package com.simonkingws.service.demos.service.impl;

import com.alibaba.fastjson2.util.DateUtils;
import com.simonkingws.api.service.BookService1FeignClient;
import com.simonkingws.api.service.FooService;
import com.simonkingws.api.service2.Book2Service;
import com.simonkingws.api.service2.BookService2FeignClient;
import io.github.simonkingws.webconfig.common.constant.DateFormatConstant;
import io.github.simonkingws.webconfig.core.annotation.InnerTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Slf4j
@RestController
public class BookService1Controller implements BookService1FeignClient {

    @Autowired
    private BookService2FeignClient bookService2FeignClient;
    @Autowired
    private Book2Service book2Service;
    @Autowired
    private FooService fooService;

    @Override
    @InnerTrace
    public String bookName(Integer bookId) {
        BigDecimal book2Price = bookService2FeignClient.getBook2Price();
        log.info("bookService2FeignClient.getBook2Price()=====> book2Price={}", book2Price);
//        BigDecimal book2Price = book2Service.getBook2Price();
//        log.info("book2Service.getBook2Price()=====> book2Price={}", book2Price);
//        int a = 1 / 0;
        fooService.foo();
        return "<<侠客行2>> --> bookId=" + bookId;
    }

    @Override
    public String checkDate(Date date) {
        log.info("checkDate:{}", DateUtils.format(date));
        return DateUtils.format(date);
    }

    @Override
    public String checkLocalDateTime(LocalDateTime localDateTime) {
        log.info("localDateTime:{}", localDateTime.format(DateTimeFormatter.ofPattern(DateFormatConstant.STANDARD_DATE_TIME)));
        return localDateTime.format(DateTimeFormatter.ofPattern(DateFormatConstant.STANDARD_DATE_TIME));
    }
}
