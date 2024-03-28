package com.simonkingws.service.demos.service.impl;

import com.simonkingws.api.service.BookService;
import com.simonkingws.api.service.FooService;
import com.simonkingws.api.service2.Book2Service;
import io.github.simonkingws.webconfig.core.annotation.InnerTrace;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * TODO
 *
 * @author: ws
 * @date: 2024/2/2 13:58
 */
@Service("bookService")
@DubboService
@Slf4j
public class BookServiceImpl implements BookService {

    @DubboReference
    private Book2Service book2Service;
    @Autowired
    private FooService fooService;


    @Override
    @InnerTrace
    public String bookName(Integer bookId) {
        BigDecimal book2Price = book2Service.getBook2Price();
        fooService.foo();
        log.info("book2Service.getBook2Price()=====> book2Price={}", book2Price);
        return "<<侠客行>> --> bookId=" + bookId;
    }
}
