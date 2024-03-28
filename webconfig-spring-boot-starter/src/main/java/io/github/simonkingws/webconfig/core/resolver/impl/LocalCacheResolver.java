package io.github.simonkingws.webconfig.core.resolver.impl;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.simonkingws.webconfig.core.resolver.CacheResolver;
import io.github.simonkingws.webconfig.core.util.LocalCacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存解析器
 *
 * @author: ws
 * @date: 2024/1/30 18:45
 */
@Component("local")
public class LocalCacheResolver implements CacheResolver {

    @Override
    public synchronized Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        Cache<String, String> stringInstance = LocalCacheUtil.getStringInstance();
        String ifPresent = stringInstance.getIfPresent(key);
        if (StringUtils.isNotBlank(ifPresent)) {
            return false;
        }

        stringInstance.policy().expireVariably().ifPresent(boundedVarExpiration  -> {
            boundedVarExpiration.put(key, value, timeout, unit);
        });
        return true;
    }

    @Override
    public void remove(String key) {
        if (StringUtils.isNotBlank(key)) {
            LocalCacheUtil.getStringInstance().invalidate(key);
        }
    }
}
