package com.scd.filesdk.tools;

import com.scd.filesdk.common.PoolType;
import com.scd.filesdk.pool.ChannelSftpPoolCreater;
import com.scd.filesdk.pool.FTPClientPoolCreater;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author chengdu
 * @date 2019/7/3.
 */
@Component
public class SingletonPoolTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingletonPoolTool.class);

    private static ChannelSftpPoolCreater channelSftpPoolCreater;

    @Autowired
    public void setChannelSftpPoolCreater(ChannelSftpPoolCreater poolCreater){
        channelSftpPoolCreater = poolCreater;
    }

    private static FTPClientPoolCreater ftpClientPoolCreater;

    @Autowired
    public void setFtpClientPoolCreater(FTPClientPoolCreater poolCreater){
        ftpClientPoolCreater = poolCreater;
    }

    public static GenericObjectPool createPool(PoolType poolType){
        switch (poolType){
            case FTP:
                return ftpClientPoolCreater.createFTPClientPool();
            case SFTP:
                return channelSftpPoolCreater.createChannelSftpPool();
            default:
                throw new NoSuchElementException("no such pooltype "+ poolType);
        }
    }

    public static void showPoolInfo(PoolType poolType){
        GenericObjectPool pool = createPool(poolType);
        if(pool != null) {
            LOGGER.info("pooltype {} maxtotal {} numactive {} numidle {} numwaiters {}",
                    poolType, pool.getMaxTotal(), pool.getNumActive(), pool.getNumIdle(), pool.getNumWaiters());
        }
    }

    public static Map<String, Object> getPoolInfo(PoolType poolType){
        GenericObjectPool pool = createPool(poolType);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pooltype", poolType);
        map.put("maxtotal", pool.getMaxTotal());
        map.put("numactive", pool.getNumActive());
        map.put("numidle", pool.getNumIdle());
        map.put("numwaiters", pool.getNumWaiters());
        return map;
    }
}
