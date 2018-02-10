package com.pramy.shiro.redis.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * IntelliJ IDEA 17
 * Created by Pramy on 2018/2/9.
 */
public class RedisCache implements Cache<Object, Object> {

    private final RedisTemplate<Object, Object> client;

    private final String keyPrefix;

    private final Long ttl;

    public RedisCache(RedisTemplate<Object, Object> client, String keyPrefix, Long ttl) {
        this.client = client;
        this.keyPrefix = keyPrefix;
        this.ttl = ttl;
    }


    private Object getKey(Object k) {
        return keyPrefix + (k == null ? "*" : k);
    }

    @Override
    public Object get(Object o) throws CacheException {
        return client.opsForValue().get(getKey(o));
    }

    @Override
    public Object put(Object o, Object o2) throws CacheException {
        if (ttl >= 0) {
            client.opsForValue().set(getKey(o), o2, ttl, TimeUnit.MILLISECONDS);
        } else {
            client.opsForValue().set(getKey(o), o2);
        }
        return o2;
    }

    @Override
    public Object remove(Object o) throws CacheException {
        Object result = get(o);
        client.delete(getKey(o));
        return result;
    }

    @Override
    public void clear() throws CacheException {
        client.delete(getKey("*"));
    }

    @Override
    public int size() {
        return keys().size();
    }

    @Override
    public Set<Object> keys() {
        return client.keys(getKey("*"));
    }

    @Override
    public Collection<Object> values() {
        return client.opsForValue().multiGet(keys());
    }
}
