package com.scd.filesdk.model.task;

import com.scd.filesdk.tools.FileMapperTool;
import com.scd.filesdk.engine.BaseEngine;
import com.scd.filesdk.model.vo.BreakMergeResult;
import com.scd.filesdk.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * @author chengdu
 * @date 2019/6/28.
 */
public class BreakTask implements Callable<BreakMergeResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BreakTask.class);

    private int chunk;

    private long chunkSize;

    private String uploadType;

    private String address;

    private File tempFile;

    public BreakTask(int chunk, long chunkSize, String uploadType, String address, File tempFile) {
        this.chunk = chunk;
        this.chunkSize = chunkSize;
        this.uploadType = uploadType;
        this.address = address;
        this.tempFile = tempFile;
    }

    @Override
    public BreakMergeResult call() throws Exception {
        BreakMergeResult breakMergeResult = new BreakMergeResult();
        breakMergeResult.setChunk(chunk);
        String fileName = tempFile.getName();
        breakMergeResult.setFileName(fileName);
        try {
            LOGGER.info("merge file {}, chunk {}, chunsize {}", fileName, chunk, chunkSize);
            BaseEngine baseEngine = FileMapperTool.getFileEngine(uploadType);
            byte[] bytes = baseEngine.downloadByte(address);
            long offset = chunk * chunkSize;
            FileUtil.mergeRemoteFile(bytes, offset, tempFile);
            breakMergeResult.setMergeStatus(true);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("merge file filename {} chunk {} error", fileName, chunk);
        }
        return breakMergeResult;
    }
}
