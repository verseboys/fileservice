package com.scd.filesdk.engine;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.scd.filesdk.config.Sftp;
import com.scd.filesdk.model.param.BreakParam;
import com.scd.filesdk.model.vo.BreakResult;
import com.scd.filesdk.util.DateUtil;
import com.scd.filesdk.util.FileUtil;
import com.scd.filesdk.util.SftpUtil;
import com.scd.filesdk.util.SftpUtilMulti;
import netscape.javascript.JSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
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
    public String upload(String filePath) throws Exception{
        InputStream inputStream = new FileInputStream(filePath);
        String filename = FileUtil.getFileName(filePath);
        return upload(inputStream, filename);
    }

    @Override
    public String upload(InputStream inputStream, String filename) throws JSchException, SftpException {
        String remotePath = sftp.getPath();
        String curDate = DateUtil.formatDatetoString(new Date(), DateUtil.YYYYMMDD);
        String destPath = remotePath + "/" + curDate;
        // 连接远程客户端
        ChannelSftp channelSftp = SftpUtilMulti.connectSftp(sftp.getHost(), sftp.getPort(),
                sftp.getUsername(), sftp.getPassword());
        // 上传文件
        return SftpUtilMulti.upload(channelSftp, inputStream, destPath, filename);
    }

    @Override
    public String upload(byte[] fbyte, String filename) throws Exception{
        String tempfilepath = "sftptemp" + "/" + filename;
        FileUtil.writeByteToFile(fbyte, tempfilepath);
        InputStream inputStream = new FileInputStream(tempfilepath);
        String remotePath = upload(inputStream, filename);
        FileUtil.deleteFile(tempfilepath);
        return remotePath;
    }

    @Override
    public InputStream download(String remotePath) throws JSchException,SftpException {
        // 连接远程客户端
        ChannelSftp channelSftp = SftpUtilMulti.connectSftp(sftp.getHost(), sftp.getPort(),
                sftp.getUsername(), sftp.getPassword());
        return SftpUtilMulti.download(channelSftp, remotePath);
    }

    @Override
    public BreakResult upload(BreakParam breakParam) {
        return null;
    }
}
