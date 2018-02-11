package com.pramyness.shiro.redis;

import com.pramyness.shiro.redis.cache.RedisCache;
import com.pramyness.shiro.redis.cache.RedisCacheManager;
import com.pramyness.shiro.redis.session.RedisSessionDao;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * IntelliJ IDEA 17
 * Created by Pramy on 2018/2/9.
 */
@Configuration
@EnableConfigurationProperties({RedisCacheProperties.class})
@ConditionalOnClass(RedisConnectionFactory.class)
public class AutoRedisConfig {

    @Autowired
    private RedisCacheProperties redisCacheProperties;

    @Bean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        KryoSerializer<Object> kryoSerializer = new KryoSerializer<>(redisCacheProperties.getSerializeTransient(),
                redisCacheProperties.getClassList());
        redisTemplate.setValueSerializer(kryoSerializer);
        redisTemplate.setKeySerializer(kryoSerializer);
        redisTemplate.setHashValueSerializer(kryoSerializer);
        redisTemplate.setHashKeySerializer(kryoSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public RedisCache redisCache(RedisTemplate<Object, Object> redisTemplate) {
        return new RedisCache(redisTemplate, redisCacheProperties.getKeyPrefix(),
                redisCacheProperties.getValueCacheExpire());
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisCache redisCache) {
        return new RedisCacheManager(redisCache);
    }

    @Bean
    @ConditionalOnWebApplication
    public SessionDAO sessionDAO(RedisTemplate<Object, Object> redisTemplate) {
        return new RedisSessionDao(redisCacheProperties.getSessionPrefix(),
                redisCacheProperties.getSessionCacheExpire(),
                redisTemplate);
    }

    @Bean
    @ConditionalOnBean(Realm.class)
    @ConditionalOnWebApplication
    public DefaultWebSecurityManager DefaultWebSecurityManager(List<Realm> realms, CacheManager cacheManager, SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealms(realms);
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(cacheManager);
        return securityManager;
    }

    @Bean
    @ConditionalOnWebApplication
    public SessionManager sessionManager(SessionDAO sessionDAO, CacheManager cacheManager) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(sessionDAO);
        long time = redisCacheProperties.getSessionTimeOut();
        sessionManager.setGlobalSessionTimeout(time > 0 ? time : RedisCacheProperties.MILLIS_PER_MINUTE * 30);
        sessionManager.setCacheManager(cacheManager);
        return sessionManager;
    }

    @Bean
    @ConditionalOnMissingBean(DefaultShiroFilterChainDefinition.class)
    @ConditionalOnWebApplication
    public DefaultShiroFilterChainDefinition chain() {
        DefaultShiroFilterChainDefinition chain = new DefaultShiroFilterChainDefinition();
        chain.addPathDefinition("/static/**", "anon");
        chain.addPathDefinitions(redisCacheProperties.getFilterChain());
        return chain;
    }

}
