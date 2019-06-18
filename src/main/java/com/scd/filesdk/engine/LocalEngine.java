package com.scd.filesdk.engine;

import com.scd.filesdk.config.Local;
import com.scd.filesdk.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
       String targetPath = storePath + File.separator + filename;
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
}
