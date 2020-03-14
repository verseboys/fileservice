package com.scd.filesdk.engine;


import com.scd.filesdk.exception.MergeFileException;
import com.scd.filesdk.model.param.BreakParam;
import com.scd.filesdk.model.param.UploadParam;
import com.scd.filesdk.model.vo.BreakResult;
import com.scd.filesdk.util.FileUtil;
import com.sun.deploy.net.DownloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author chengdu
 */
public abstract class BaseEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseEngine.class);

    public abstract String upload(String filePath) throws Exception;

    public abstract String upload(InputStream inputStream, String filename) throws Exception ;

    public abstract String upload(InputStream inputStream, UploadParam uploadParam) throws Exception;

    public abstract String upload(byte[] fbyte, String filename) throws Exception;

    public abstract InputStream download(String remotePath) throws Exception;

    public abstract BreakResult upload(BreakParam breakParam);

    /**
     * 获取下载的 byte[] 文件
     * @param remotePath
     * @return
     * @throws Exception
     */
    public byte[] downloadByte(String remotePath) {
        String tempPath = "temp" + File.separator + UUID.randomUUID().toString() + ".tmp";
        byte[] bytes = null;
        try {
            InputStream inputStream = download(remotePath);
            LOGGER.info(" thread name {}, filepath {}", Thread.currentThread().getName(), tempPath);
            FileUtil.writeInputStreamToLocal(inputStream, tempPath);
            bytes = FileUtil.getBytes(tempPath);
        } catch (Exception e) {
        } finally {
            FileUtil.deleteFile(tempPath);
        }
        if (bytes == null) {
            throw new MergeFileException("down load file to byte error");
        }
        return bytes;
    }
}
