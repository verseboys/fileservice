package com.scd.filesdk.pool;

import com.scd.filesdk.config.FtpPool;
import com.scd.filesdk.exception.ConstructorPoolException;
import com.scd.filesdk.factory.FTPClientFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chengdu
 * @date 2019/7/3.
 */
@Component
public class FTPClientPoolCreater {

    private static final Logger LOGGER = LoggerFactory.getLogger(FTPClientPoolCreater.class);

    @Autowired
    private FtpPool ftpPool;

    @Autowired
    private FTPClientFactory ftpClientFactory;

    private FTPClientPool<FTPClient> ftpClientPool;

    private final Lock lock = new ReentrantLock();

    public FTPClientPool<FTPClient> createFTPClientPool(){
        lock.lock();
        try {
            if (ftpClientPool == null) {
                GenericObjectPoolConfig config = new GenericObjectPoolConfig();
                config.setMaxTotal(ftpPool.getMaxTotal());
                config.setMaxIdle(ftpPool.getMaxIdle());
                config.setTestWhileIdle(ftpPool.isTestWhileIdle());
                config.setTestOnBorrow(ftpPool.isTestOnOnBorrow());
                config.setMaxWaitMillis(ftpPool.getMaxWait());
                config.setTimeBetweenEvictionRunsMillis(ftpPool.getTimeBetweenEvictionRunsMillis());
                LOGGER.info("create new pool {}", "ftpClientPool");
                ftpClientPool = new FTPClientPool(ftpClientFactory, config);
            }
        }catch (Exception e){
            throw new ConstructorPoolException("create FTPClientPool error");
        }finally {
            lock.unlock();
        }
        return ftpClientPool;
    }
}
