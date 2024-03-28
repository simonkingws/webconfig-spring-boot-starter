package com.simonkingws.service.demos.service.impl;

import com.simonkingws.api.service.FooService;
import io.github.simonkingws.webconfig.core.annotation.InnerTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author: ws
 * @date: 2024/2/27 10:26
 */
@Slf4j
@Service("fooService")
public class FooServiceImpl implements FooService {

    @Override
    @InnerTrace
    public void foo() {
        log.info("~~~~~~~~~~~~~~~~~ foo() 执行了 ~~~~~~~~~~~~~~~~~");
        int a = 1 / 0;
    }
}
