package io.github.simonkingws.webconfig.core;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.simonkingws.webconfig.core.util.LocalCacheUtil;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

class WebconfigCoreSpringBootStarterApplicationTests {

    @Test
    void contextLoads() throws InterruptedException {
        Cache<String, String> stringInstance = LocalCacheUtil.getStringInstance();
        String ifPresent = stringInstance.getIfPresent("1");
        System.out.println(ifPresent);
        stringInstance.policy().expireVariably().ifPresent(boundedVarExpiration  -> {
            boundedVarExpiration.put("1", "requestCode", 2, TimeUnit.SECONDS);
        });
        ifPresent = stringInstance.getIfPresent("1");
        System.out.println(ifPresent);



//        stringInstance.put("1", "requestCode");
//        stringInstance.put("2", "requestCode2");
//        stringInstance.put("3", "requestCode3");
        stringInstance.policy().expireVariably().ifPresent(boundedVarExpiration  -> {
            boundedVarExpiration.put("1", "requestCode", 2, TimeUnit.SECONDS);
            boundedVarExpiration.put("2", "requestCode2", 5, TimeUnit.SECONDS);
            boundedVarExpiration.put("3", "requestCode3", 6, TimeUnit.SECONDS);
        });

        for(int i = 0;i < 7;i++){
//            Thread.sleep(6000);
//            stringInstance.invalidate("3");
            System.out.println("第一轮数据：---------------------------------");
            System.out.println(new Date() + "****1:" + stringInstance.getIfPresent("1") );
            System.out.println(new Date() + "****2:" + stringInstance.getIfPresent("2"));
            System.out.println(new Date() + "****3:" + stringInstance.getIfPresent("3"));
                        Thread.sleep(1000);

        }
    }
}
