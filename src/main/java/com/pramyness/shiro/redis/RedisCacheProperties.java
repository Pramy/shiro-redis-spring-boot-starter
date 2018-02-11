package com.pramyness.shiro.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * IntelliJ IDEA 17
 * Created by Pramy on 2018/2/9.
 */
    @ConfigurationProperties(prefix = "spring.shiro.redis")
public class RedisCacheProperties {

    public static final long MILLIS_PER_SECOND = 1000;
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    public static final long MILLIS_DAY = 24 * MILLIS_PER_HOUR;


    private String keyPrefix = "shiro:cache:";

    private String sessionPrefix = "shiro:session:";
    /**
     * Time unit：millis
     */
    private Long sessionTimeOut = 30 * MILLIS_PER_MINUTE;
    /**
     * Time unit：millis
     */
    private Long sessionCacheExpire = MILLIS_DAY;

    /**
     * Time unit：millis
     */
    private Long valueCacheExpire = -1L;

    private boolean isSerializeTransient=true;

    private Map<String,String> filterChain ;

    private List<Class<?>> classList ;

    public Map<String,String> getFilterChain() {
        return filterChain;
    }

    public void setFilterChain(Map<String,String> filterChain) {
        this.filterChain = filterChain;
    }

    public List<Class<?>> getClassList() {
        return classList;
    }

    public void setClassList(List<Class<?>> classList) {
        this.classList = classList;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getSessionPrefix() {
        return sessionPrefix;
    }

    public void setSessionPrefix(String sessionPrefix) {
        this.sessionPrefix = sessionPrefix;
    }

    public Long getSessionCacheExpire() {
        return sessionCacheExpire;
    }

    public void setSessionCacheExpire(Long sessionCacheExpire) {
        this.sessionCacheExpire = sessionCacheExpire;
    }

    public Long getValueCacheExpire() {
        return valueCacheExpire;
    }

    public void setValueCacheExpire(Long valueCacheExpire) {
        this.valueCacheExpire = valueCacheExpire;
    }

    public Long getSessionTimeOut() {
        return sessionTimeOut;
    }

    public void setSessionTimeOut(Long sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

    public boolean getSerializeTransient() {
        return isSerializeTransient;
    }

    public void setSerializeTransient(boolean serializeTransient) {
        isSerializeTransient = serializeTransient;
    }
}
