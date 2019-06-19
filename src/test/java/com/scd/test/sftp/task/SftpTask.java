package com.scd.test.sftp.task;

import com.jcraft.jsch.ChannelSftp;
import com.scd.filesdk.util.FileUtil;
import com.scd.filesdk.util.SftpUtil;
import com.scd.filesdk.util.SftpUtilMulti;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * @author chengdu
 * @date 2019/6/19.
 */
public class SftpTask implements Callable<String> {

    public static final String host = "192.168.1.104";
    public static final int port = 22;
    public static final String username = "test";
    public static final String password = "test";

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
        String result = SftpUtil.upload(filepath, "/sftpupload", filename);
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

    @Override
    public String call() throws Exception {
//        return testSftpUtil();
        return testSftpMulti();
    }
}
