package com.scd.filesdk.engine;

import com.scd.filesdk.common.PoolType;
import com.scd.filesdk.config.Ftp;
import com.scd.filesdk.model.param.BreakParam;
import com.scd.filesdk.model.vo.BreakResult;
import com.scd.filesdk.tools.SingletonPoolTool;
import com.scd.filesdk.util.FileUtil;
import com.scd.filesdk.util.FtpUtilMulti;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * @author chengdu
 * @date 2019/6/20.
 */
@Component
public class FtpEngine extends BaseEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpEngine.class);

    @Autowired
    private Ftp ftp;

    @Override
    public String upload(String filePath) throws Exception {
        String filename = FileUtil.getFileName(filePath);
        InputStream inputStream = new FileInputStream(filePath);
        return upload(inputStream, filename);
    }

    @Override
    public String upload(InputStream inputStream, String filename) throws Exception {
        String destPath = FileUtil.getDestPath(ftp.getPath());
        return uploadFile(inputStream, destPath, filename);
    }

    @Override
    public String upload(byte[] fbyte, String filename) throws Exception {
        String ftptemp = "ftptemp" + "/" + filename;
        FileUtil.writeByteToFile(fbyte, ftptemp);
        InputStream inputStream = new FileInputStream(ftptemp);
        String remotepath = upload(inputStream, filename);
        FileUtil.deleteFile(ftptemp);
        return remotepath;
    }

    @Override
    public InputStream download(String remotePath) throws IOException {
        // 使用连接池中的连接下载 会存在问题 work dir null ???
        // let jvm do it, haha
        FTPClient ftpClient = FtpUtilMulti.connectFtp(ftp.getHost(), ftp.getPort(),
                ftp.getUsername(), ftp.getPassword());
        return FtpUtilMulti.download(ftpClient, remotePath);
//        return downloadFile(remotePath);
    }

    @Override
    public BreakResult upload(BreakParam breakParam) {
        BreakResult breakResult = new BreakResult();
        String originFileName = breakParam.getName();
        int chunk = breakParam.getChunk();
        long chunkSize = breakParam.getChunkSize();
        try {
            LOGGER.info("【Ftp】 filename : {}, chunk : {}, chunksize : {}", originFileName, chunk, chunkSize);
            InputStream inputStream = breakParam.getFile().getInputStream();
            String destPath = FileUtil.getDestPath(ftp.getPath());
            String fileName =  chunk + "_" + chunkSize + "_" + originFileName;
            String storePath = uploadFile(inputStream, destPath, fileName);
            breakResult.setFilePath(storePath);
            breakResult.setWriteSuccess(true);
        }catch (Exception e){
            LOGGER.error("upload chunk file to Ftp error filename : {} chunk : {}", originFileName, breakParam.getChunk());
            breakResult.setWriteSuccess(false);
        }
        return breakResult;
    }

    private String uploadFile(InputStream inputStream,
                             String destPath, String filename){
        String remotePath;
        FTPClient ftpClient = null;
        GenericObjectPool ftpPool= null;
        try {
            ftpPool = SingletonPoolTool.createPool(PoolType.FTP);
            ftpClient = (FTPClient) ftpPool.borrowObject();
            remotePath = FtpUtilMulti.upload(ftpClient, inputStream, destPath, filename);
        }catch (Exception e){
            throw new RuntimeException("upload file to ftp error, filename "+filename);
        }finally {
            ftpPool.returnObject(ftpClient);
            SingletonPoolTool.showPoolInfo(PoolType.FTP);
        }
        return remotePath;
    }

    private InputStream downloadFile(String remotePath){
        FTPClient ftpClient = null;
        GenericObjectPool ftpPool= null;
        InputStream inputStream;
        try {
            ftpPool = SingletonPoolTool.createPool(PoolType.FTP);
            ftpClient = (FTPClient) ftpPool.borrowObject();
            inputStream = FtpUtilMulti.download(ftpClient, remotePath);
        }catch (Exception e){
            throw new RuntimeException("upload file from ftp error, filename "+remotePath);
        }finally {
            ftpPool.returnObject(ftpClient);
            SingletonPoolTool.showPoolInfo(PoolType.FTP);
        }
        return inputStream;
    }

}
