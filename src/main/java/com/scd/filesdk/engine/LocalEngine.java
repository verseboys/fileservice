package com.scd.filesdk.engine;

import com.scd.filesdk.config.Local;
import com.scd.filesdk.model.param.BreakParam;
import com.scd.filesdk.model.vo.BreakResult;
import com.scd.filesdk.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @author chengdu
 */
@Component
public class LocalEngine extends BaseEngine{

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalEngine.class);

    @Autowired
    private Local local;

    @Override
    public String upload(String filePath) throws IOException{
        String storePath = local.getPath();
        String filename = FileUtil.getFileName(filePath);
        String targetPath = storePath + File.separator + filename;
        FileUtil.copyFile(filePath, targetPath);
        return targetPath;
    }

    @Override
    public String upload(InputStream inputStream, String filename) throws IOException {
       String storePath = local.getPath();
       String targetPath = storePath + "/" + filename;
       FileUtil.writeInputStreamToLocal(inputStream, targetPath);
       return targetPath;
    }

    @Override
    public String upload(byte[] fbyte, String filename) throws IOException {
        String storePath = local.getPath();
        String targetPath = storePath + File.separator + filename;
        FileUtil.writeByteToFile(fbyte, targetPath);
        return targetPath;
    }

    @Override
    public InputStream download(String remotePath) throws FileNotFoundException{
        File file = new File(remotePath);
        if(!file.exists() || !file.isFile()){
            LOGGER.error("file not exists {}",remotePath);
            throw new FileNotFoundException("file not exists "+remotePath);
        }
        return new FileInputStream(remotePath);
    }

    @Override
    public BreakResult upload(BreakParam param) {
        String fileName = param.getName();
        String tempDirPath = local.getPath() + "/" + param.getMd5();
        String tempFileName = fileName + "_tmp";
        File tmpDir = new File(tempDirPath);
        File tmpFile = new File(tempDirPath, tempFileName);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        int chunk = param.getChunk();
        long chunkSize = param.getChunkSize();
        long offset = chunk * chunkSize;
        LOGGER.info("【Local】 filename : {}, chunk : {}, chunksize : {}", fileName, chunk, chunkSize);
        BreakResult breakResult = new BreakResult();
        try {
            RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
            //定位到该分片的偏移量
            accessTmpFile.seek(offset);
            //写入该分片数据
            accessTmpFile.write(param.getFile().getBytes());
            // 关闭随机读取文件
            accessTmpFile.close();
            breakResult.setFilePath(tmpFile.getAbsolutePath());
            breakResult.setWriteSuccess(true);
            breakResult.setTempFile(tmpFile);
        }catch (Exception e){
            LOGGER.error("upload chunk file to Local error filename : {} chunk : {}", fileName, chunk);
            breakResult.setWriteSuccess(false);
        }
        return breakResult;
    }
}
