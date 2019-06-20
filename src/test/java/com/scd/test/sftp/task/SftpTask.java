package com.scd.test.sftp.task;

import com.jcraft.jsch.ChannelSftp;
import com.scd.filesdk.common.ServiceInfo;
import com.scd.filesdk.util.*;
import org.apache.commons.net.ftp.FTPClient;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * @author chengdu
 * @date 2019/6/19.
 */
public class SftpTask implements Callable<String> {

//    public static final String host = ServiceInfo.SFTP.host;
//    public static final int port = ServiceInfo.SFTP.port;
//    public static final String username = ServiceInfo.SFTP.username;
//    public static final String password = ServiceInfo.SFTP.password;

    public static final String host = ServiceInfo.FTP.host;
    public static final int port = ServiceInfo.FTP.port;
    public static final String username = ServiceInfo.FTP.username;
    public static final String password = ServiceInfo.FTP.password;

    private String filepath;

    public SftpTask(String filepath){
        this.filepath = filepath;
    }

    /**
     * 多线程环境 ERROR
     * @return
     * @throws Exception
     */
    public String testSftpUtil() throws Exception {
        SftpUtil.connectSftp(host, port, username, password);
        InputStream inputStream = new FileInputStream(filepath);
        String filename = FileUtil.getFileName(filepath);
        Thread.sleep(3000);
        System.out.println("Thread:"+Thread.currentThread().getName() + " FileName:"+filepath);
        String result = SftpUtil.upload(inputStream, "/sftpupload", filename);
        SftpUtil.sftpQuit();
        return result;
    }

    public String testSftpMulti() throws Exception {
        ChannelSftp channelSftp = SftpUtilMulti.connectSftp(host, port, username, password);
        InputStream inputStream = new FileInputStream(filepath);
        String filename = FileUtil.getFileName(filepath);
        System.out.println("Thread:"+Thread.currentThread().getName() + " FileName:"+filepath);
        String result = SftpUtilMulti.upload(channelSftp, inputStream,"/sftpuploadtest", filename);
        Thread.sleep(3000);
        SftpUtilMulti.sftpQuit(channelSftp);
        return result;
    }

    public String testFtpUtil() throws Exception {
        FtpUtil.connectFtp(host, port, username, password);
        InputStream inputStream = new FileInputStream(filepath);
        String filename = FileUtil.getFileName(filepath);
        System.out.println("Thread:"+Thread.currentThread().getName() + " FileName:"+filepath);
        String result = FtpUtil.upload(inputStream, "/ftpupload", filename);
        FtpUtil.ftpQuit();
        return result;
    }

    public String testFtpMulti() throws Exception {
        FTPClient ftpClient = FtpUtilMulti.connectFtp(host, port, username, password);
        InputStream inputStream = new FileInputStream(filepath);
        String filename = FileUtil.getFileName(filepath);
        System.out.println("Thread:"+Thread.currentThread().getName() + " FileName:"+filepath);
        String result = FtpUtilMulti.upload(ftpClient, inputStream, "/ftpupload", filename);
        FtpUtilMulti.ftpQuit(ftpClient);
        return result;
    }

    @Override
    public String call() throws Exception {
//        return testSftpUtil();
//        return testSftpMulti();
//        return testFtpUtil();
        return testFtpMulti();
    }
}
