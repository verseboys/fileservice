package com.scd.filesdk.engine;

import com.mongodb.client.gridfs.GridFSBucket;
import com.scd.filesdk.model.param.BreakParam;
import com.scd.filesdk.model.vo.BreakResult;
import com.scd.filesdk.util.FileUtil;
import com.scd.filesdk.util.SftpUtilMulti;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author chengdu
 * @date 2019/6/24.
 */
@Component
public class MongoEngine extends BaseEngine{

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoEngine.class);

    @Autowired
    private GridFSBucket gridFSBucket;


    @Override
    public String upload(String filePath) throws Exception {
        String filename = FileUtil.getFileName(filePath);
        InputStream inputStream = new FileInputStream(filePath);
        return upload(inputStream, filename);
    }

    @Override
    public String upload(InputStream inputStream, String filename) throws Exception {
        ObjectId objectId = gridFSBucket.uploadFromStream(filename, inputStream);
        return objectId.toHexString();
    }

    @Override
    public String upload(byte[] fbyte, String filename) throws Exception {
        String ftptemp = "mongotemp" + "/" + filename;
        FileUtil.writeByteToFile(fbyte, ftptemp);
        InputStream inputStream = new FileInputStream(ftptemp);
        String remotepath = upload(inputStream, filename);
        FileUtil.deleteFile(ftptemp);
        return remotepath;
    }

    @Override
    public InputStream download(String remotePath) throws Exception {
        ObjectId objectId = new ObjectId(remotePath);
        return gridFSBucket.openDownloadStream(objectId);
    }

    @Override
    public BreakResult upload(BreakParam breakParam) {
        BreakResult breakResult = new BreakResult();
        String originFileName = breakParam.getName();
        int curChunk = breakParam.getChunk();
        try {
            // 上传文件
            InputStream inputStream = breakParam.getFile().getInputStream();
            // 合并文件时好标识
            String fileName =  curChunk + "_" + breakParam.getChunkSize() + "_" + originFileName;
            String storePath = upload(inputStream, fileName);
            breakResult.setWriteSuccess(true);
            breakResult.setFilePath(storePath);
        }catch (Exception e){
            LOGGER.error("upload chunk file to Mongo error filename : {} chunk : {}", originFileName, curChunk);
            breakResult.setWriteSuccess(false);
        }
        return breakResult;
    }
}
