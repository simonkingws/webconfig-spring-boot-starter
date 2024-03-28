package com.simonkingws.application.demos.web;

import com.simonkingws.api.service.BookService;
import com.simonkingws.api.service2.Book2Service;
import io.github.simonkingws.webconfig.common.constant.DateFormatConstant;
import io.github.simonkingws.webconfig.common.util.RequestHolder;
import io.github.simonkingws.webconfig.core.annotation.PrintParams;
import io.github.simonkingws.webconfig.core.annotation.RequestLimiting;
import io.github.simonkingws.webconfig.core.annotation.SubmitLimiting;
import io.github.simonkingws.webconfig.core.contant.Policy;
import io.github.simonkingws.webconfig.core.contant.RunMode;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * 测试控制类
 *
 * @author ws
 * @date 2024/2/1 13:59
 */
@RestController
@RequestMapping("foo")
public class BasicController {

    @DubboReference
    private BookService bookService;
    @DubboReference
    private Book2Service book2Service;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 测试Form表单提交
     */
    @RequestMapping("/testFormData")
    @PrintParams
    public String testFormData(@Valid Book book){
        RequestHolder.get().setUserId("123456");
        RequestHolder.get().setUserName("test");
        System.out.println(book);
        System.out.println(DateTimeFormatter.ofPattern(DateFormatConstant.STANDARD_DATE_TIME).format(book.getFinishDateTime()));
        return "success";
    }

    /**
     * 测试JSON
     */
    @RequestMapping("/testJSON")
    public String testJSON(@RequestBody @Validated Book book){
        System.out.println(book);
        System.out.println(DateTimeFormatter.ofPattern(DateFormatConstant.STANDARD_DATE_TIME).format(book.getFinishDateTime()));
        return "success";
    }

    /**
     * 测试@RequestLimiting
     */
    @RequestMapping("/testLimiting")
    @RequestLimiting(returnPolicy = Policy.IGNORE)
    public String testLimiting(Book book) throws InterruptedException {
        Thread.sleep(2000);
        System.out.println(book);
        return "success";
    }

    /**
     * 测试dubbo
     *
     * @author ws
     * @date 2024/2/2 14:12
     */
    @RequestMapping("/testDubbo3")
    public String getBookName(Integer bookId)  {
        return bookService.bookName(bookId);
    }

    @RequestMapping("/testDubbo4")
    public String testDubbo4()  {
        return book2Service.getBook2Price().toString();
    }

    @RequestMapping("/testSubmitInit")
    @SubmitLimiting(mode = RunMode.INIT)
    public String testSubmitInit()  {
        return "init success";
    }

    @RequestMapping("/testSubmitValid")
    @SubmitLimiting
    public String testSubmitValid()  {
        int i = 1 / 0;
        return "submitValid success";
    }

    @RequestMapping("/testLua")
    public String testLua()  {
        stringRedisTemplate.opsForValue().set("aaa", "xxxxx");
        String luascript = "local aaa = redis.call('get',KEYS[1]); redis.call('del',KEYS[1]); return aaa;";
        System.out.println("qq:" + stringRedisTemplate.execute(RedisScript.of(luascript, String.class), Collections.singletonList("aaa")));
        System.out.println("qqaa:" + stringRedisTemplate.opsForValue().get("aaa"));
        return "testLua success";
    }
}
