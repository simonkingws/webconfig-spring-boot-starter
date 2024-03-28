package io.github.simonkingws.webconfig.core.resolver;

import java.util.concurrent.TimeUnit;

/**
 * 缓存解析器
 *
 * @author: ws
 * @date: 2024/1/30 18:41
 */
public interface CacheResolver {

    /**
     * key 不存在时设置
     *
     * @author ws
     * @date 2024/2/5 10:26
     */
    Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit);

    /**
     * 删除Key
     *
     * @author ws
     * @date 2024/2/5 10:26
     */
    void remove(String key);
}
