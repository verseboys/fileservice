package com.scd.filesdk.engine;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.scd.filesdk.common.PoolType;
import com.scd.filesdk.config.Sftp;
import com.scd.filesdk.model.param.BreakParam;
import com.scd.filesdk.model.vo.BreakResult;
import com.scd.filesdk.tools.SingletonPoolTool;
import com.scd.filesdk.util.FileUtil;
import com.scd.filesdk.util.SftpUtilMulti;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;

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
        String destPath = FileUtil.getDestPath(sftp.getPath());
        // 上传文件
        return uploadFile(inputStream, destPath, filename);
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
        return downloadFile(remotePath);
    }

    @Override
    public BreakResult upload(BreakParam breakParam) {
        BreakResult breakResult = new BreakResult();
        String originFileName = breakParam.getName();
        int curChunk = breakParam.getChunk();
        long chunkSize = breakParam.getChunkSize();
        try {
            LOGGER.info("【Sftp】 filename : {}, chunk : {}, chunksize : {}", originFileName, curChunk, chunkSize);
            InputStream inputStream = breakParam.getFile().getInputStream();
            String destPath = FileUtil.getDestPath(sftp.getPath());
            String fileName = curChunk + "_" + chunkSize + "_" + originFileName;
            String storePath = uploadFile(inputStream, destPath, fileName);
            breakResult.setWriteSuccess(true);
            breakResult.setFilePath(storePath);
        }catch (Exception e){
            LOGGER.error("upload chunk file to Sftp error filename : {} chunk : {}", originFileName, curChunk);
            breakResult.setWriteSuccess(false);
        }
        return breakResult;
    }


    private String uploadFile(InputStream inputStream,
                           String destPath, String filename){
        String remotePath;
        GenericObjectPool sftpPool = null;
        ChannelSftp channelSftp = null;
        try {
            sftpPool = SingletonPoolTool.createPool(PoolType.SFTP);
            channelSftp = (ChannelSftp) sftpPool.borrowObject();
            remotePath = SftpUtilMulti.upload(channelSftp, inputStream, destPath, filename);
        }catch (Exception e){
            throw new RuntimeException("upload file to sftp error, filename "+filename);
        }finally {
            sftpPool.returnObject(channelSftp);
            SingletonPoolTool.showPoolInfo(PoolType.SFTP);
        }
        return remotePath;
    }

    private InputStream downloadFile(String remotePath){
        GenericObjectPool sftpPool = null;
        ChannelSftp channelSftp = null;
        InputStream inputStream;
        try {
            sftpPool = SingletonPoolTool.createPool(PoolType.SFTP);
            channelSftp = (ChannelSftp) sftpPool.borrowObject();
            inputStream = SftpUtilMulti.download(channelSftp, remotePath);
        }catch (Exception e){
            throw new RuntimeException("download file from sftp error, filename "+remotePath);
        }finally {
            sftpPool.returnObject(channelSftp);
            SingletonPoolTool.showPoolInfo(PoolType.SFTP);
        }
        return inputStream;
    }
}
