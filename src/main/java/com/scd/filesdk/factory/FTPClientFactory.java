package com.scd.filesdk.factory;

import com.scd.filesdk.config.Ftp;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author chengdu
 * @date 2019/7/2.
 */
@Component
public class FTPClientFactory extends BasePooledObjectFactory<FTPClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FTPClientFactory.class);

    @Autowired
    private Ftp ftp;

    @Override
    public FTPClient create() throws Exception {
        return createFtpClient();
    }


    @Override
    public PooledObject<FTPClient> wrap(FTPClient ftpClient) {
        return new DefaultPooledObject<>(ftpClient);
    }

    @Override
    public void activateObject(PooledObject<FTPClient> p) throws Exception {
        FTPClient ftpClient = p.getObject();
        if(ftpClient != null && !ftpClient.isConnected()){
            connectFtp(ftpClient);
        }
    }

    @Override
    public void passivateObject(PooledObject<FTPClient> p) throws Exception {
        LOGGER.info("passivate Object");
        FTPClient ftpClient = p.getObject();
        ftpClient.reinitialize();
    }

    @Override
    public boolean validateObject(PooledObject<FTPClient> p) {
        FTPClient ftpClient = p.getObject();
        if(ftpClient.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public void destroyObject(PooledObject<FTPClient> p) throws Exception {
        FTPClient ftpClient = p.getObject();
        if(ftpClient != null && ftpClient.isConnected()){
            ftpClient.logout();
        }
    }


    private FTPClient createFtpClient() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.enterLocalPassiveMode();
        ftpClient.setControlEncoding(StandardCharsets.UTF_8.name());
        ftpClient.setDataTimeout(2000);
        connectFtp(ftpClient);
        return ftpClient;
    }

    private void connectFtp(FTPClient ftpClient) throws IOException {
        // 连接ftp服务器
        ftpClient.connect(ftp.getHost(), ftp.getPort());
        // 登录ftp服务器
        ftpClient.login(ftp.getUsername(), ftp.getPassword());
        int replycode = ftpClient.getReplyCode();
        if(! FTPReply.isPositiveCompletion(replycode)){
            throw new RuntimeException("can not connect ftp");
        }else{
            LOGGER.info("connect ftp success");
        }
        // 登录之后设置
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    }
}
