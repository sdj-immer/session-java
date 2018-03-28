package cn.immer.session.core.session.http.redis;

import cn.immer.session.core.session.MapSession;
import cn.immer.session.core.session.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis session repository.
 *
 * @author sdj
 */
public class RedisSessionRepository implements SessionRepository<MapSession> {
    private static Logger LOG = LoggerFactory.getLogger(RedisSessionRepository.class);

    private int timeout = (int) TimeUnit.DAYS.toSeconds(10);

    private JedisPool pool;//

    public RedisSessionRepository(JedisPool pool) {
        this.pool = pool;
    }

    public MapSession createSession() {

        return null;
    }

    public void save(MapSession session) {

    }

    public MapSession findById(String id) {

        return null;
    }

    public void deleteById(String id) {

    }
}
