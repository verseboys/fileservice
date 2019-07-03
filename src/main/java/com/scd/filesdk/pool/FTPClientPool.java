package com.scd.filesdk.pool;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;

/**
 * @author chengdu
 * @date 2019/7/2.
 */
public class FTPClientPool<FTPClient> extends GenericObjectPool<FTPClient> {

    public FTPClientPool(PooledObjectFactory<FTPClient> factory) {
        super(factory);
    }

    public FTPClientPool(PooledObjectFactory<FTPClient> factory, GenericObjectPoolConfig<FTPClient> config) {
        super(factory, config);
    }

    public FTPClientPool(PooledObjectFactory<FTPClient> factory, GenericObjectPoolConfig<FTPClient> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
