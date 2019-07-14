package com.scd.filesdk.engine;

import com.scd.filesdk.config.Fdfs;
import com.scd.filesdk.model.param.BreakParam;
import com.scd.filesdk.model.param.UploadParam;
import com.scd.filesdk.model.vo.BreakResult;
import com.scd.filesdk.util.FdfsUtil;
import com.scd.filesdk.util.FileUtil;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author chengdu
 * @date 2019/7/14.
 */
@Component
public class FdfsEngine extends BaseEngine {

    @Autowired
    private Fdfs fdfs;

    @Override
    public String upload(String filePath) throws Exception {
        String filename = FileUtil.getFileName(filePath);
        InputStream inputStream = new FileInputStream(filePath);
        return upload(inputStream, filename);
    }

    @Override
    public String upload(InputStream inputStream, String filename) throws Exception {
        StorageClient storageClient = FdfsUtil.connectFdfs(fdfs.getConfig());
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String[] result = FdfsUtil.upload(storageClient, bytes, fdfs.getGroup(),filename);
        return result[0] + "," + result[1];
    }

    @Override
    public String upload(InputStream inputStream, UploadParam uploadParam) throws Exception {
        StorageClient storageClient = FdfsUtil.connectFdfs(fdfs.getConfig());
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String groupName = "";
        if(! StringUtils.isEmpty(uploadParam)){
            groupName = uploadParam.getGroupName();
        }
        String[] result = FdfsUtil.upload(storageClient, bytes, groupName,uploadParam.getFileName());
        return result[0] + "," + result[1];
    }

    @Override
    public String upload(byte[] fbyte, String filename) throws Exception {
        String ftptemp = "fdfstemp" + "/" + filename;
        FileUtil.writeByteToFile(fbyte, ftptemp);
        InputStream inputStream = new FileInputStream(ftptemp);
        String remotepath = upload(inputStream, filename);
        FileUtil.deleteFile(ftptemp);
        return remotepath;
    }

    @Override
    public InputStream download(String remotePath) throws Exception {
        return null;
    }

    @Override
    public BreakResult upload(BreakParam breakParam) {
        return null;
    }
}
