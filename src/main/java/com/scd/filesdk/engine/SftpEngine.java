package com.scd.filesdk.engine;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.scd.filesdk.config.Sftp;
import com.scd.filesdk.model.param.BreakParam;
import com.scd.filesdk.model.vo.BreakResult;
import com.scd.filesdk.util.FileUtil;
import com.scd.filesdk.util.SftpUtilMulti;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author chengdu
 */
@Component
public class SftpEngine extends BaseEngine{

    private static final Logger LOGGER = LoggerFactory.getLogger(SftpEngine.class);

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
        // 连接远程客户端
        ChannelSftp channelSftp = SftpUtilMulti.connectSftp(sftp.getHost(), sftp.getPort(),
                sftp.getUsername(), sftp.getPassword());
        String destPath = FileUtil.getDestPath(sftp.getPath());
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
        BreakResult breakResult = new BreakResult();
        String originFileName = breakParam.getName();
        int curChunk = breakParam.getChunk();
        long chunkSize = breakParam.getChunkSize();
        try {
            LOGGER.info("【Sftp】 filename : {}, chunk : {}, chunksize : {}", originFileName, curChunk, chunkSize);
            // 连接远程客户端
            ChannelSftp channelSftp = SftpUtilMulti.connectSftp(sftp.getHost(), sftp.getPort(),
                    sftp.getUsername(), sftp.getPassword());
            InputStream inputStream = breakParam.getFile().getInputStream();
            String destPath = FileUtil.getDestPath(sftp.getPath());
            String fileName = curChunk + "_" + chunkSize + "_" + originFileName;
            // 上传文件
            String storePath = SftpUtilMulti.upload(channelSftp, inputStream, destPath, fileName);
            breakResult.setWriteSuccess(true);
            breakResult.setFilePath(storePath);
        }catch (Exception e){
            LOGGER.error("upload chunk file to Sftp error filename : {} chunk : {}", originFileName, curChunk);
            breakResult.setWriteSuccess(false);
        }
        return breakResult;
    }
}
