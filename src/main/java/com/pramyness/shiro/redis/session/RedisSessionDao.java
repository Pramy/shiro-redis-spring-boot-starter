package com.pramyness.shiro.redis.session;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * IntelliJ IDEA 17
 * Created by Pramy on 2018/2/9.
 */
public class RedisSessionDao extends AbstractSessionDAO {

    private final Logger logger = LoggerFactory.getLogger(RedisSessionDao.class);

    private String keyPrefix;

    private Long ttl;

    private RedisTemplate<Object, Object> client;

    private Cache<Serializable, Session> caches;

    public RedisSessionDao(String keyPrefix, Long ttl, RedisTemplate<Object, Object> client) {
        init(keyPrefix, ttl, client);
    }

    @Override
    protected Serializable doCreate(Session session) {

        logger.debug("doCreate");
        if (session == null) {
            logger.error("session is null");
            throw new SessionException("session is null");
        }
        Serializable id = super.generateSessionId(session);
        ((SimpleSession) session).setId(id);
        saveSession(session);
        return id;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            logger.error("session id is null");
            throw new SessionException("session id is null");
        }
        Session session = caches.getIfPresent(sessionId);
        if (session == null) {
            logger.debug("doReadSession " + sessionId + " from redis");
            session = (Session) client.opsForValue().get(getKey(sessionId));
            if (session != null) {
                caches.put(sessionId, session);
            }
        }
        return session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {

        if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
            delete(session);
        } else {
            logger.debug("update" + session.getId() + " session");
            saveSession(session);
        }
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            logger.error("session or session id is null");
            throw new UnknownSessionException("session or session id is null");
        }
        caches.invalidate(session.getId());

        logger.debug("delete " + session.getId() + " from redis");
        Object key = getKey(session.getId());
        client.delete(key);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<Object> keys = client.keys(getKey("*"));
        List<Object> values = client.opsForValue().multiGet(keys);
        List<Session> result = new ArrayList<>(values.size());
        for (Object o :
                values) {
            result.add((Session) o);
        }
        return result;
    }

    private void saveSession(Session session) {
        if (session == null || session.getId() == null) {
            logger.error("session or session id is null");
            throw new UnknownSessionException("session or session id is null");
        }

        Object key = getKey(session.getId());
        logger.debug("save session to redis");
        client.opsForValue().set(key, session, ttl, TimeUnit.MILLISECONDS);
        caches.put(session.getId(), session);
    }

    private Object getKey(Object o) {
        return keyPrefix + (o == null ? "*" : o);
    }


    private void init(String keyPrefix, Long ttl, RedisTemplate<Object, Object> client) {
        this.keyPrefix = keyPrefix;
        if (ttl > 0) {
            this.ttl = ttl;
        } else {
            this.ttl = 0L;
            logger.debug("session expire must be more than 0");
        }
        this.client = client;
        caches = CacheBuilder.newBuilder()
                .initialCapacity(500)
                .weakValues()
                .maximumSize(1000)
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build();
    }
}
