package com.scd.filesdk.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author chengdu
 * @date 2019/6/20.
 */
public class FtpUtilMulti {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpUtilMulti.class);

    public static FTPClient connectFtp(String host, int port, String username, String password) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.enterLocalPassiveMode();
        // 连接ftp服务器
        ftpClient.connect(host, port);
        // 登录ftp服务器
        ftpClient.login(username, password);
        int replycode = ftpClient.getReplyCode();
        if(!FTPReply.isPositiveCompletion(replycode)){
            throw new RuntimeException("can not connect ftp");
        }else{
            LOGGER.info("connect ftp success");
        }
        ftpClient.setControlEncoding(StandardCharsets.UTF_8.name());
        ftpClient.setDataTimeout(2000);
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        return ftpClient;
    }

    public static void makeDirAndChange(FTPClient ftpClient, String filepath) throws IOException {
        filepath.replace("\\","/");
        String[] filepaths = filepath.split("/");
        for(String fpath : filepaths) {
            fpath = fpath.trim();
            if(fpath.length() == 0){
                continue;
            }
            if(! ftpClient.changeWorkingDirectory(fpath)){
                ftpClient.makeDirectory(fpath);
                LOGGER.info("make dir "+fpath);
                ftpClient.changeWorkingDirectory(fpath);
                LOGGER.info("change work dir "+fpath);
            }else{
                LOGGER.info("change work dir "+fpath);
            }
            LOGGER.info("work dir " +ftpClient.printWorkingDirectory());
        }
    }

    public static String upload(FTPClient ftpClient, InputStream inputStream,
                                String remotePath, String filename) throws IOException {
        makeDirAndChange(ftpClient, remotePath);
        ftpClient.storeFile(filename, inputStream);
        return ftpClient.printWorkingDirectory() + "/" + filename;
    }

    public static void ftpQuit(FTPClient ftpClient){
        if(ftpClient.isConnected()){
            try {
                ftpClient.logout();
                ftpClient.quit();
            }catch (Exception e){
                LOGGER.error("quit ftp error");
            }
        }
    }

    /**
     * 下载文件
     * @param ftpClient
     * @param remotePath
     * @return
     * @throws IOException
     */
    public static InputStream download(FTPClient ftpClient, String remotePath) throws IOException{
        return ftpClient.retrieveFileStream(remotePath);
    }
}
