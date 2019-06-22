package com.scd.filesdk.util;

import com.scd.filesdk.common.ServiceInfo;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author chengdu
 * @date 2019/6/20.
 */
public class FtpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpUtil.class);

    private static FTPClient ftpClient = null;

    public static void connectFtp(String host, int port, String username, String password) throws IOException {
        if(ftpClient.isConnected()){
            return ;
        }
        ftpClient = new FTPClient();
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
    }

    public static void makeDirAndChange(String filepath) throws IOException {
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

    public static String upload(InputStream inputStream, String remotePath, String filename) throws IOException {
        makeDirAndChange(remotePath);
        ftpClient.storeFile(filename, inputStream);
        return ftpClient.printWorkingDirectory() + "/" + filename;
    }

    public static void ftpQuit(){
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
     * @param remotePath
     * @return
     * @throws IOException
     */
    public InputStream download(String remotePath) throws IOException {
        return ftpClient.retrieveFileStream(remotePath);
    }

    public static void main(String[] args) throws IOException {
        String host = ServiceInfo.FTP.host;
        int port = ServiceInfo.FTP.port;
        String username = ServiceInfo.FTP.username;
        String password = ServiceInfo.FTP.password;
        connectFtp(host, port, username, password);
        String filepath = "C:\\Users\\chengdu\\Desktop\\filetype\\jcommon-1.0.16.jar";
        InputStream inputStream = new FileInputStream(filepath);
        String filename = FileUtil.getFileName(filepath);
        String remotePath = "/ftp";
        System.out.println(upload(inputStream, remotePath, filename));
    }
}
