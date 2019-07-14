package com.scd.filesdk.factory;

import com.scd.filesdk.config.Fdfs;
import com.scd.filesdk.util.FdfsUtil;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.csource.fastdfs.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Socket;

/**
 * @author chengdu
 * @date 2019/7/14.
 */

@Component
public class FdfsClientFactory extends BasePooledObjectFactory<StorageClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FdfsClientFactory.class);

    @Autowired
    private Fdfs fdfs;

    @Override
    public StorageClient create() throws Exception {
        LOGGER.debug("create StorageClient");
        return FdfsUtil.connectFdfs(fdfs.getConfig());
    }

    @Override
    public PooledObject<StorageClient> wrap(StorageClient storageClient) {
        return new DefaultPooledObject<>(storageClient);
    }

    public void destroyObject(PooledObject<StorageClient> p) throws Exception {
        LOGGER.debug("destory StorageClient");
        StorageClient storageClient = p.getObject();
        storageClient.close();
    }

    public boolean validateObject(PooledObject<StorageClient> p) {
        LOGGER.debug("validate StorageClient");
        StorageClient storageClient = p.getObject();
        if(storageClient != null && storageClient.isAvaliable()){
            return true;
        }
        return false;
    }

    public void activateObject(PooledObject<StorageClient> p) throws Exception {
        LOGGER.debug("activate StorageClient");
        StorageClient storageClient = p.getObject();
        if(!storageClient.isConnected()) {
            Socket socket = storageClient.getTrackerServer().getSocket();
            if(socket.isConnected()){
                LOGGER.debug("active socket success");
            }
        }else{
            LOGGER.debug("socket connected");
        }
    }


    public void passivateObject(PooledObject<StorageClient> p) throws Exception {
        LOGGER.debug("passivate StorageClient");
    }
}
