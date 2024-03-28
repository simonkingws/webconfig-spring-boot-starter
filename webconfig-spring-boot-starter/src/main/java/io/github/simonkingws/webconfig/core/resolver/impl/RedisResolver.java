package io.github.simonkingws.webconfig.core.resolver.impl;

import io.github.simonkingws.webconfig.core.resolver.CacheResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis缓存解析器
 *
 * @author: ws
 * @date: 2024/1/30 18:47
 */
@Component("redis")
public class RedisResolver implements CacheResolver {

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        if (stringRedisTemplate != null) {
            return stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        }
        return false;
    }

    @Override
    public void remove(String key) {
        if (stringRedisTemplate != null && StringUtils.isNotBlank(key)) {
            stringRedisTemplate.delete(key);
        }
    }
}
