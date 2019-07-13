package com.scd.filesdk.util;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author chengdu
 * @date 2019/7/13.
 */
public class FdfsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FdfsUtil.class);


    public static StorageClient connectFdfs(String confPath) throws Exception {
        ClientGlobal.init(confPath);
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        LOGGER.info("connect fdfs success");
        return storageClient;
    }

    public static String[] upload(StorageClient storageClient,byte[] bytes,
                                  String groupName, String fileName) throws Exception {
        NameValuePair[] metaList = new NameValuePair[1];
        metaList[0] = new NameValuePair("fileName", fileName);
        String[] result = uploadFile(storageClient, groupName, bytes,null, metaList);
        return result;
    }

    public static String[] uploadFile(StorageClient storageClient, String group_name, byte[] bytes, String file_ext_name,
                                  NameValuePair[] meta_list) throws Exception{
       return storageClient.upload_file(group_name, bytes, 0, bytes.length, file_ext_name, meta_list);
    }


    public static byte[] download(StorageClient storageClient, String groupName, String fileName) throws Exception{
        return storageClient.download_file(groupName, fileName);
    }

    public static void disConnect(StorageClient storageClient)  {
        if(storageClient != null){
            try {
                storageClient.close();
                LOGGER.debug("disconnect client");
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String configPath = "fdfs/fdfsclient.conf";
        String filename = "build.PNG";
        File file = new File("C:/Users/chengdu/Desktop/build.PNG");
        InputStream inputStream = new FileInputStream(file);
        int length = inputStream.available();
        byte[] bytes = new byte[length];
        inputStream.read(bytes);
        StorageClient storageClient = connectFdfs(configPath);
        String[] result = upload(storageClient, bytes, "group1", filename);
        LOGGER.info("upload result {}", Arrays.asList(result));
        byte[] downbytes = download(storageClient, result[0], result[1]);
        LOGGER.info("download result {}", downbytes.length);
        disConnect(storageClient);

    }
}
