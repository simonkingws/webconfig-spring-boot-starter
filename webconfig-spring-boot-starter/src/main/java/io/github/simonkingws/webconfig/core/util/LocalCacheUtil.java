package io.github.simonkingws.webconfig.core.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 本地缓存工具类
 *
 * @author: ws
 * @date: 2024/1/30 17:44
 */
public class LocalCacheUtil {

    private static Cache<String, String> instance = null;

    private LocalCacheUtil() {

    }

    public static synchronized Cache<String, String> getStringInstance(){
        if (instance != null) {
            return instance;
        }
        return instance = Caffeine.newBuilder().maximumSize(50_000).expireAfter(new Expiry<String, String>() {
            @Override
            public long expireAfterCreate(@NonNull String s, @NonNull String s2, long l) {
                return 10000;
            }

            @Override
            public long expireAfterUpdate(@NonNull String s, @NonNull String s2, long l, @NonNegative long l1) {
                return l1;
            }

            @Override
            public long expireAfterRead(@NonNull String s, @NonNull String s2, long l, @NonNegative long l1) {
                return l1;
            }
        }).build();
    }
}
