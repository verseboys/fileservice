package com.scd.filesdk.pool;

import com.jcraft.jsch.ChannelSftp;
import com.scd.filesdk.config.SftpPool;
import com.scd.filesdk.factory.ChannelSftpFactory;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chengdu
 * @date 2019/7/2.
 */
public class ChannelSftpPool<ChannelSftp> extends GenericObjectPool<ChannelSftp> {

    public ChannelSftpPool(PooledObjectFactory<ChannelSftp> factory) {
        super(factory);
    }

    public ChannelSftpPool(PooledObjectFactory<ChannelSftp> factory, GenericObjectPoolConfig<ChannelSftp> config) {
        super(factory, config);
    }

    public ChannelSftpPool(PooledObjectFactory<ChannelSftp> factory, GenericObjectPoolConfig<ChannelSftp> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
