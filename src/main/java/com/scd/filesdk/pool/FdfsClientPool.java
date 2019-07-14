package com.scd.filesdk.pool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.fastdfs.StorageClient;

/**
 * @author chengdu
 * @date 2019/7/14.
 */
public class FdfsClientPool<StorageClient> extends GenericObjectPool<StorageClient> {

    public FdfsClientPool(PooledObjectFactory<StorageClient> factory) {
        super(factory);
    }

    public FdfsClientPool(PooledObjectFactory<StorageClient> factory, GenericObjectPoolConfig<StorageClient> config) {
        super(factory, config);
    }

    public FdfsClientPool(PooledObjectFactory<StorageClient> factory, GenericObjectPoolConfig<StorageClient> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
