package com.scd.test.mongo;

import com.mongodb.client.gridfs.GridFSBucket;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * @author chengdu
 * @date 2019/7/14.
 */
public class MongoTask implements Callable<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoTask.class);

    private static GridFSBucket gridFSBucket;

    public static void setGridFSBucket(GridFSBucket bucket){
        gridFSBucket = bucket;
    }

    private String fileName;

    private InputStream inputStream;

    public MongoTask(String fileName, FileInputStream fileInputStream){
        this.fileName = fileName;
        inputStream = fileInputStream;
    }

    @Override
    public String call() throws Exception {
        LOGGER.info(Thread.currentThread().getName() + " upload file {} time {}", fileName, System.currentTimeMillis());
        ObjectId objectId = gridFSBucket.uploadFromStream(fileName, inputStream);
        return objectId.toHexString();
    }
}
