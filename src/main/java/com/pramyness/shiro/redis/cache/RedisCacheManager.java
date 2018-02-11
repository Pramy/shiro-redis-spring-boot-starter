package com.pramyness.shiro.redis.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * IntelliJ IDEA 17
 * Created by Pramy on 2018/2/9.
 */
public class RedisCacheManager implements CacheManager {

    private final RedisCache client;

    public RedisCacheManager(RedisCache client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return (Cache<K, V>) client;
    }


}
