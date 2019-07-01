package com.scd.thread.apachepool;

import com.scd.thread.conn.SftpConn;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.BaseGenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author chengdu
 * @date 2019/6/30.
 */
public class SftpConnectionPool extends GenericObjectPool<SftpConn> {

    public SftpConnectionPool(PooledObjectFactory<SftpConn> factory) {
        super(factory);
    }

    public SftpConnectionPool(PooledObjectFactory<SftpConn> factory, GenericObjectPoolConfig config) {
        super(factory, config);
    }

    public SftpConnectionPool(PooledObjectFactory<SftpConn> factory, GenericObjectPoolConfig config,
                              AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
