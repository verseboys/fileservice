package com.scd.filesdk.engine;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.scd.filesdk.config.Sftp;
import com.scd.filesdk.util.DateUtil;
import com.scd.filesdk.util.SftpUtil;
import netscape.javascript.JSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author chengdu
 */
@Component
public class SftpEngine extends BaseEngine{

    @Autowired
    private Sftp sftp;


    @Override
    public String upload(String filePath) {
        return "I am SftpEngine";
    }

    @Override
    public String upload(InputStream inputStream, String filename) throws JSchException, SftpException {
        String remotePath = sftp.getPath();
        String curDate = DateUtil.formatDatetoString(new Date(), DateUtil.YYYYMMDD);
        String destPath = remotePath + "/" + curDate;
        // 连接远程客户端
        SftpUtil.connectSftp(sftp.getHost(), sftp.getPort(), sftp.getUsername(), sftp.getPassword());
        // 上传文件
        String uploaddir = SftpUtil.upload(inputStream, destPath, filename);
        return uploaddir + "/" + filename;
    }

    @Override
    public String upload(byte[] fbyte, String filename) throws IOException{
        return null;
    }
}
