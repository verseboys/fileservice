package com.scd.test.hdfs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author James Chen
 * @date 20/07/19
 */
public class HdfsTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HdfsTask.class);

    private InputStream inputStream;
    private String remotePath;
    private String fileName;
    private int index;
    private HdfsUploadResult[] result;
    private CountDownLatch countDownLatch;

    public HdfsTask(InputStream inputStream, String remotePath, String fileName,
                    int index, HdfsUploadResult[] result, CountDownLatch countDownLatch) {
        this.inputStream = inputStream;
        this.remotePath = remotePath;
        this.fileName = fileName;
        this.index = index;
        this.result = result;
        this.countDownLatch = countDownLatch;
    }


    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        LOGGER.info("Thread Name {}, Start Time {}", Thread.currentThread().getName(), startTime);
        HdfsUploadResult hdfsUploadResult = new HdfsUploadResult();
        hdfsUploadResult.setFileName(fileName);
        try {
            String filePath = HdfsCallBack.uploadFileToHdfs(inputStream, remotePath, fileName);
            hdfsUploadResult.setTime(System.currentTimeMillis() - startTime);
            if(filePath != null && filePath.length() != 0){
                hdfsUploadResult.setStorePath(filePath);
                hdfsUploadResult.setStatus(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            HdfsCallBack.progressResult(result, index, hdfsUploadResult);
            countDownLatch.countDown();
        }
    }
}
