package com.scd.filesdk.factory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.scd.filesdk.common.ServiceInfo;
import com.scd.filesdk.config.Sftp;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author chengdu
 * @date 2019/7/2.
 */
@Component
public class ChannelSftpFactory extends BasePooledObjectFactory<ChannelSftp> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelSftpFactory.class);

    @Autowired
    private Sftp sftp;

    @Override
    public ChannelSftp create() throws Exception {
        JSch jsch = new JSch();
        Session sshSession = jsch.getSession(sftp.getUsername(), sftp.getHost(), sftp.getPort());
        sshSession.setPassword(sftp.getPassword());
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        // Kerberos 验证
        sshConfig.put("PreferredAuthentications","publickey,keyboard-interactive,password");
        sshSession.setConfig(sshConfig);
        sshSession.connect();
        LOGGER.info("SFTP Session connected.");
        Channel channel = sshSession.openChannel("sftp");
        return (ChannelSftp) channel;
    }

    @Override
    public PooledObject<ChannelSftp> wrap(ChannelSftp channelSftp) {
        return new DefaultPooledObject<>(channelSftp);
    }

    public void destroyObject(PooledObject<ChannelSftp> p) throws Exception {
        ChannelSftp channelSftp = p.getObject();
        Session session = channelSftp.getSession();
        if(session != null && session.isConnected()){
            session.disconnect();
        }
        if(channelSftp.isConnected()){
            channelSftp.quit();
        }
    }

    public boolean validateObject(PooledObject<ChannelSftp> p) {
        ChannelSftp channelSftp = p.getObject();
        LOGGER.info("validate Object {}", channelSftp.isConnected());
        if(channelSftp != null && channelSftp.isConnected()){
            return true;
        }
        return false;
    }

    public void activateObject(PooledObject<ChannelSftp> p) throws Exception {
        ChannelSftp channelSftp = p.getObject();
        LOGGER.info("activate Object {}", channelSftp.isConnected());
        if(!channelSftp.isConnected()){
            channelSftp.connect();
        }
    }


    public void passivateObject(PooledObject<ChannelSftp> p) throws Exception {
        ChannelSftp channelSftp = p.getObject();
        LOGGER.info("passivate Object {}", channelSftp.isConnected());
        // sftp 为何要断开才行
        // 哈哈，同一个连接创建多级文件夹问题
//        if(channelSftp.isConnected()){
//            channelSftp.getSession().disconnect();
//        }
    }
}
