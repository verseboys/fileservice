package com.scd.test.hdfs;

import com.scd.filesdk.util.FdfsUtil;
import org.csource.fastdfs.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * @author chengdu
 * @date 2019/7/14.
 */
public class FdfsTask implements Callable<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FdfsTask.class);

    private String fileName;

    private InputStream inputStream;

    public FdfsTask(String filename, InputStream fileInputStream){
        fileName = filename;
        inputStream = fileInputStream;
    }

    @Override
    public String call() throws Exception {
        LOGGER.info(Thread.currentThread().getName() + " upload to fdfs, file {} time {}", fileName, System.currentTimeMillis());
        String configPath = "fdfs/fdfsclient.conf";
        StorageClient storageClient = null;
        String uploadresult = null;
        try {
            storageClient = FdfsUtil.connectFdfs(configPath);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String[] result = FdfsUtil.upload(storageClient, bytes, "group1", fileName);
            uploadresult =  result[0] + "," + result[1];
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            FdfsUtil.disConnect(storageClient);
        }
        return uploadresult;
    }
}
