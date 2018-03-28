package cn.immer.session.core.session;

import cn.immer.session.core.session.http.redis.RedisSessionRepository;
import cn.immer.session.redis.RedisSessionRepository;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * default session repository factory implement.
 *
 * @author sdj
 */
public class DefaultSessionRepositoryFactory implements SessionRepositoryFactory {
    private SessionRepository repository;

    public DefaultSessionRepositoryFactory() {
        repository = new RedisSessionRepository(new JedisPool(new JedisPoolConfig(), "redis.host.name"));
    }

    @Override
    public SessionRepository getSessionRepository() {
        return repository;
    }
}
