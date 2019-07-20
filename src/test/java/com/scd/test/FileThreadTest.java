package com.scd.test;

import com.scd.filesdk.util.FileUtil;
import com.scd.test.fdfs.FdfsTask;
import com.scd.test.hdfs.HdfsTask;
import com.scd.test.hdfs.HdfsUploadResult;
import com.scd.test.sftp.task.SftpTask;
import io.swagger.models.auth.In;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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

    private ExecutorService threadPool = Executors.newFixedThreadPool(15);

    @Test
    public void testFtpSftp() throws Exception {
        List<String> filePaths = new ArrayList<>(15);
        FileUtil.getFilePaths(basePath, filePaths);
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

    @Test
    public void testHdfs() throws Exception {
        String filedir = "/home/james/code/github/commons-imaging/src/test/data/images/bmp";
        List<String> filePaths = new ArrayList<>();
        FileUtil.getFilePaths(filedir, filePaths);
        HdfsUploadResult[] hdfsUploadResults = new HdfsUploadResult[filePaths.size()];
        CountDownLatch countDownLatch = new CountDownLatch(filePaths.size());
        for(int i = 0; i < filePaths.size(); i++){
            InputStream inputStream = new FileInputStream(filePaths.get(i));
            String fileName = FileUtil.getFileName(filePaths.get(i));
            HdfsTask hdfsTask = new HdfsTask(inputStream, "/hdfs/test", fileName, i,
                    hdfsUploadResults, countDownLatch);
            threadPool.execute(hdfsTask);
        }
        countDownLatch.await();
        LOGGER.info("upload result {}", Arrays.asList(hdfsUploadResults));
        threadPool.shutdown();
    }
}
