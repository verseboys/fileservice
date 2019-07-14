package com.scd.test;

import com.scd.filesdk.util.FileUtil;
import com.scd.test.hdfs.FdfsTask;
import com.scd.test.sftp.task.SftpTask;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author chengdu
 * @date 2019/6/19.
 */
public class FileThreadTest {


    private static final String basePath = "C:\\Users\\chengdu\\Desktop\\filetype";

    private static final Logger LOGGER = LoggerFactory.getLogger(FileThreadTest.class);

    @Test
    public void testFtpSftp() throws Exception {
        List<String> filePaths = new ArrayList<>(15);
        FileUtil.getFilePaths(basePath, filePaths);
        ExecutorService threadPool = Executors.newFixedThreadPool(15);
        List<Future<String>> futureList = new ArrayList<>(filePaths.size());
        for(String filepath : filePaths){
            SftpTask sftpTask = new SftpTask(filepath);
            Future<String> stringFuture = threadPool.submit(sftpTask);
            futureList.add(stringFuture);
        }

        for(Future<String> stringFuture : futureList){
            try {
                LOGGER.info("upload result {}", stringFuture.get());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        threadPool.shutdown();
    }

    @Test
    public void testFdfs() throws Exception {
        List<String> filePaths = new ArrayList<>(15);
        FileUtil.getFilePaths(basePath, filePaths);
        ExecutorService threadPool = Executors.newFixedThreadPool(15);
        List<Future<String>> futureList = new ArrayList<>(filePaths.size());
        for(String filepath : filePaths){
            InputStream inputStream = new FileInputStream(filepath);
            String fileName = FileUtil.getFileName(filepath);
            FdfsTask fdfsTask = new FdfsTask(fileName, inputStream);
            Future<String> stringFuture = threadPool.submit(fdfsTask);
            futureList.add(stringFuture);
        }

        for(Future<String> stringFuture : futureList){
            try {
                LOGGER.info("upload result {}", stringFuture.get());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        threadPool.shutdown();
    }
}
