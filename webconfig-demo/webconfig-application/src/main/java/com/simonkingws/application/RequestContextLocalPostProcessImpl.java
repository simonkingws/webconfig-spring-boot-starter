package com.simonkingws.application;

import com.alibaba.fastjson.JSON;
import io.github.simonkingws.webconfig.common.context.RequestContextLocal;
import io.github.simonkingws.webconfig.common.process.RequestContextLocalPostProcess;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author: ws
 * @date: 2024/2/4 15:35
 */
@Service("requestContextLocalPostProcess")
@DubboService
@Slf4j
public class RequestContextLocalPostProcessImpl implements RequestContextLocalPostProcess {
    @Override
    public void afterRequestContextLocal(RequestContextLocal requestContextLocal) {
        log.info("》》》》》》》》》》》》》》》》afterRequestContextLocal:{}", JSON.toJSONString(requestContextLocal));
    }

    @Override
    public void destroy(RequestContextLocal finalRequestContextLocal) {
        log.info("》》》》》》》》》》》》》》》》destroy:{}",  JSON.toJSONString(finalRequestContextLocal));
    }
}
