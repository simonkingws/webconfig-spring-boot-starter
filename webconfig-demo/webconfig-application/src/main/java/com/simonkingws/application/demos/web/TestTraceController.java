package com.simonkingws.application.demos.web;

import com.alibaba.fastjson.JSON;
import io.github.simonkingws.webconfig.common.context.TraceItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 测试Feign接口
 *
 * @author: ws
 * @date: 2024/2/5 17:16
 */
@Slf4j
@RestController
@RequestMapping("/trace")
public class TestTraceController {


    /**
     * 测试Fiegn
     *
     * @author ws
     * @date 2024/2/5 16:11
     * @param
     * @return {@link String}
     */
    @RequestMapping("/collect")
    public String collect(@RequestBody List<TraceItem> traceItems)  {
        log.info("/trace/collec: {}>>>>>>>>>>>>>>>>>>>>>>>>", JSON.toJSONString(traceItems));
        System.exit(0);
        return "succcess";
    }
}
