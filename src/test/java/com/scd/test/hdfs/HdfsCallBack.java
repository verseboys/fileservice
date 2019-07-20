package com.scd.test.hdfs;

import com.scd.filesdk.util.HdfsUtil;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author James Chen
 * @date 20/07/19
 */
public class HdfsCallBack {

    private static String url = "hdfs://192.168.1.101:9000";
    private static String userName = "scd";

    private static final Logger LOGGER = LoggerFactory.getLogger(HdfsCallBack.class);

    public static String uploadFileToHdfs(InputStream inputStream,
                                          String remotePath, String fileName){
        String result = "";
        FileSystem fs = null;
        try{
            fs = HdfsUtil.initFsUrl(url, userName);
            result = HdfsUtil.upload(fs, inputStream, remotePath, fileName);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try{
                    inputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if(fs != null) {
                HdfsUtil.closeFileSystem(fs);
            }
        }
        return result;
    }

    public static void progressResult(HdfsUploadResult[] hdfsUploadResults, int index, HdfsUploadResult hdfsUploadResult){
        if(index > hdfsUploadResults.length - 1){
            return ;
        }
        LOGGER.info("index {} result {}", index, hdfsUploadResult);
        hdfsUploadResults[index] = hdfsUploadResult;
    }
}
