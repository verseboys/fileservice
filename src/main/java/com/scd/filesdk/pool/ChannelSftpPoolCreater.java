package com.scd.filesdk.pool;

import com.jcraft.jsch.ChannelSftp;
import com.scd.filesdk.config.SftpPool;
import com.scd.filesdk.exception.ConstructorPoolException;
import com.scd.filesdk.factory.ChannelSftpFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chengdu
 * @date 2019/7/2.
 */
@Component
public class ChannelSftpPoolCreater {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelSftpPoolCreater.class);

    @Autowired
    private ChannelSftpFactory channelSftpFactory;

    @Autowired
    private SftpPool sftpPool;

    private ChannelSftpPool<ChannelSftp> channelSftpPool;

    private Lock lock = new ReentrantLock();

    /**
     * 单例模式 singleton
     * @return
     */
    public ChannelSftpPool<ChannelSftp> createChannelSftpPool(){
        lock.lock();
        try {
            if (channelSftpPool == null) {
                GenericObjectPoolConfig<ChannelSftp> config = new GenericObjectPoolConfig<>();
                config.setMaxTotal(sftpPool.getMaxTotal());
                config.setMaxIdle(sftpPool.getMaxIdle());
                config.setTestWhileIdle(sftpPool.isTestWhileIdle());
                config.setMaxWaitMillis(sftpPool.getMaxWait());
                config.setTimeBetweenEvictionRunsMillis(sftpPool.getTimeBetweenEvictionRunsMillis());
                LOGGER.info("create new pool {}", "channelSftpPool");
                channelSftpPool = new ChannelSftpPool(channelSftpFactory, config);
            }
//            else{
//                LOGGER.info("channelSftpPool exists");
//            }
        } catch(Exception e){
            throw new ConstructorPoolException("create ChannelSftpPool error");
        } finally {
            lock.unlock();
        }
        return channelSftpPool;
    }
}
