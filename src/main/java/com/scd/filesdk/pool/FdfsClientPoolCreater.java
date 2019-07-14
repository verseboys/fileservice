package com.scd.filesdk.pool;

import com.scd.filesdk.config.FdfsPool;
import com.scd.filesdk.exception.ConstructorPoolException;
import com.scd.filesdk.factory.FdfsClientFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.fastdfs.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chengdu
 * @date 2019/7/14.
 */
@Component
public class FdfsClientPoolCreater {

    private static final Logger LOGGER = LoggerFactory.getLogger(FdfsClientPoolCreater.class);

    @Autowired
    private FdfsPool fdfsPool;

    @Autowired
    private FdfsClientFactory fdfsClientFactory;

    private FdfsClientPool<StorageClient> fdfsClientPool;

    private Lock lock = new ReentrantLock();

    public FdfsClientPool<StorageClient> createFdfdClientPool(){
        if(fdfsClientPool == null){
            lock.lock();
            try{
                if(fdfsClientPool == null){
                    GenericObjectPoolConfig<StorageClient> config = new GenericObjectPoolConfig<>();
                    config.setMaxTotal(fdfsPool.getMaxTotal());
                    config.setMaxIdle(fdfsPool.getMaxIdle());
                    config.setTestWhileIdle(fdfsPool.isTestWhileIdle());
                    config.setTestOnBorrow(fdfsPool.isTestOnOnBorrow());
                    config.setMaxWaitMillis(fdfsPool.getMaxWait());
                    config.setTimeBetweenEvictionRunsMillis(fdfsPool.getTimeBetweenEvictionRunsMillis());
                    LOGGER.info("create new pool {}", "fdfsClientPool");
                    fdfsClientPool = new FdfsClientPool<StorageClient>(fdfsClientFactory, config);
                }
            }catch (Exception e){
                throw new ConstructorPoolException("create FdfsClientPool error");
            }finally {
                lock.unlock();
            }
        }
        return fdfsClientPool;
    }

}
