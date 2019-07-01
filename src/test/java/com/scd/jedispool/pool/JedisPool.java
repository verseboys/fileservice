package com.scd.jedispool.pool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;

/**
 * @author chengdu
 * @date 2019/7/1.
 */
public class JedisPool extends GenericObjectPool<Jedis> {

    public JedisPool(PooledObjectFactory<Jedis> factory) {
        super(factory);
    }

    public JedisPool(PooledObjectFactory<Jedis> factory, GenericObjectPoolConfig<Jedis> config) {
        super(factory, config);
    }

    public JedisPool(PooledObjectFactory<Jedis> factory, GenericObjectPoolConfig<Jedis> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
