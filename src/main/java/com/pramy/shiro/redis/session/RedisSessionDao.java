package com.pramy.shiro.redis.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
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

    private final String keyPrefix;

    private final Long ttl;

    private final RedisTemplate<Object, Object> client;


    public RedisSessionDao(String keyPrefix, Long ttl, RedisTemplate<Object, Object> client) {
        this.keyPrefix = keyPrefix;
        if (ttl > 0) {
            this.ttl = ttl;
        } else {
            this.ttl = 0L;
            logger.debug("session expire must be more than 0");
        }
        this.client = client;
    }

    @Override
    protected Serializable doCreate(Session session) {
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
        return (Session) client.opsForValue().get(getKey(sessionId));
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        saveSession(session);
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            logger.error("session or session id is null");
            throw new UnknownSessionException("session or session id is null");
        }
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
        client.opsForValue().set(key, session, ttl, TimeUnit.MILLISECONDS);
    }

    private Object getKey(Object o) {
        return keyPrefix + (o == null ? "*" : o);
    }

}
