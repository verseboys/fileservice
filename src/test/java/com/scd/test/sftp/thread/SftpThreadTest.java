package com.scd.test.sftp.thread;

import com.scd.filesdk.util.FileUtil;
import com.scd.filesdk.util.SftpUtil;
import com.scd.test.sftp.task.SftpTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author chengdu
 * @date 2019/6/19.
 */
public class SftpThreadTest {

    public static final String host = "192.168.1.104";
    public static final int port = 22;
    public static final String username = "test";
    public static final String password = "test";

    public static void main(String[] args) throws Exception {
        String basePath = "C:\\Users\\chengdu\\Desktop\\filetype";
        List<String> filePaths = new ArrayList<>(10);
        FileUtil.getFilePaths(basePath, filePaths);
//        for(String filepath : filePaths){
//            SftpUtil.connectSftp(host, port,username, password);
//            String uploadpath = SftpUtil.upload(filepath, "/sftpupload", FileUtil.getFileName(filepath));
//            System.out.println(uploadpath);
//        }
        ExecutorService threadPool = Executors.newFixedThreadPool(30);
        List<Future<String>> futureList = new ArrayList<>(filePaths.size());
        for(String filepath : filePaths){
            SftpTask sftpTask = new SftpTask(filepath);
            Future<String> stringFuture = threadPool.submit(sftpTask);
            futureList.add(stringFuture);
        }

        for(Future<String> stringFuture : futureList){
            System.out.println(stringFuture.get());
        }
        threadPool.shutdown();
    }
}
