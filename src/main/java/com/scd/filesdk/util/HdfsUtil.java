package com.scd.filesdk.util;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author James Chen
 * @date 20/07/19
 */
public class HdfsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HdfsUtil.class);

    /**
     * 初始化 hdfs
     * init hdfs FileSystem
     * @param url
     * @param userName
     * @return
     * @throws IOException
     */
    public static FileSystem initFsUrl(String url, String userName) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", url);
        System.setProperty("HADOOP_USER_NAME", userName);
        return FileSystem.get(conf);
    }

    /**
     * upload file to hdfs by inputstream
     * @param fs
     * @param inputStream
     * @param remotePath
     * @param fileName
     * @return file store path in hdfs
     * @throws IOException
     */
    public static String upload(FileSystem fs, InputStream inputStream,
                                String remotePath, String fileName) throws IOException {
        createRemotePath(fs, remotePath);
        String fileStorePath = remotePath + "/" + fileName;
        FSDataOutputStream outputStream = fs.create(new Path(fileStorePath), true);
        IOUtils.copy(inputStream, outputStream);
        LOGGER.debug("upload file success");
        return fileStorePath;
    }

    /**
     * upload file to fdfs by file path
     * @param fs
     * @param filePath
     * @param remotePath
     * @param fileName
     * @return file store path in hdfs
     * @throws IOException
     */
    public static String upload(FileSystem fs, String filePath, String remotePath, String fileName) throws IOException{
        String fileStorePath = remotePath + "/" + fileName;
        fs.copyFromLocalFile(new Path(filePath), new Path(fileStorePath));
        return fileStorePath;
    }

    /**
     * download file from hdfs
     * @param fs
     * @param remotePath
     * @return
     * @throws IOException
     */
    public static InputStream download(FileSystem fs, String remotePath) throws IOException {
        return fs.open(new Path(remotePath));
    }

    /**
     * create dir
     * @param fs
     * @param path
     * @return
     * @throws IOException
     */
    public static boolean createRemotePath(FileSystem fs, String path) throws IOException{
        Path remotePath = new Path(path);
        boolean result = true;
        if(!fs.exists(remotePath)){
            result = fs.mkdirs(remotePath);
        }
        LOGGER.debug("file exists");
        return result;
    }

    /**
     * delete directory or file
     * @param fs
     * @param filePath
     * @throws IOException
     */
    public static boolean deleteFile(FileSystem fs, String filePath) throws IOException {
        Path path = new Path(filePath);
        return fs.delete(path, true);
    }

    /**
     * 关闭 hdfs
     * @param fileSystem
     */
    public static void closeFileSystem(FileSystem fileSystem){
        try {
            fileSystem.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
         String corePath = "hdfs/core-site.xml";
         String url = "hdfs://192.168.1.101:9000";
         FileSystem fileSystem = initFsUrl(url, "scd");

         // upload
//         String filePath = "/home/james/Data/SoftWare/core-site.xml";
//         FileInputStream fileInputStream = new FileInputStream(filePath);
//         System.out.println(upload(fileSystem, fileInputStream, "/aaaa/111", "core-site-1.xml"));
//         closeFileSystem(fileSystem);
         // download
           String remotePath = "/aaaa/111/core-site-1.xml";
           InputStream inputStream = download(fileSystem, remotePath);
           String localPath = "/home/james/Data/SoftWare/cc/core-site.xml";
           FileUtil.writeInputStreamToLocal(inputStream, localPath);
    }
}
