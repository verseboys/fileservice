package com.scd.fileservice.service.impl;

import com.scd.filesdk.conversion.FileEngineConversion;
import com.scd.filesdk.engine.BaseEngine;
import com.scd.fileservice.common.CommonConstant;
import com.scd.fileservice.data.FileRedisData;
import com.scd.fileservice.service.FileService;
import com.scd.fileservice.utils.FileDownLoadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author chengdu
 * @date 2019/6/22.
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRedisData fileRedisData;

    @Override
    public String upload(MultipartFile multipartFile, String type) throws Exception {
        InputStream inputStream = multipartFile.getInputStream();
        String fileName = multipartFile.getOriginalFilename();
        BaseEngine baseEngine = FileEngineConversion.convertFileEngine(type);
        String remotePath = baseEngine.upload(inputStream, fileName);
        Map<String, String> fileInfo = createFileInfo(multipartFile, type, remotePath);
        String fileId = UUID.randomUUID().toString();
        fileRedisData.saveFileInfo(fileId, fileInfo);
        fileRedisData.saveFileId(fileId);
        return fileId;
    }

    @Override
    public String download(String fileId, HttpServletResponse response) throws Exception {
        if(!fileRedisData.existsFileId(fileId)){
            return "file not exists";
        }
        Map<Object, Object> fileInfoMap = fileRedisData.findFileInfo(fileId);
        Object uploadtype = fileInfoMap.get(CommonConstant.FILEINFO.uploadtype.getValue());
        Object remotePath = fileInfoMap.get(CommonConstant.FILEINFO.fileaddress.getValue());
        Object fileName = fileInfoMap.get(CommonConstant.FILEINFO.filename.getValue());
        if(StringUtils.isEmpty(uploadtype) || StringUtils.isEmpty(remotePath)
                || StringUtils.isEmpty(fileName)){
            return "data exception";
        }
        BaseEngine baseEngine = FileEngineConversion.convertFileEngine(uploadtype.toString());
        // 下载文件
        InputStream inputStream = baseEngine.download(remotePath.toString());
        // 输出文件
        FileDownLoadUtil.outputFile(response, inputStream, fileName.toString());
        return "";
    }

    private Map<String,String> createFileInfo(MultipartFile multipartFile, String type, String remotePath) {
        String fileName = multipartFile.getOriginalFilename();
        long fileSize = multipartFile.getSize();
        Map<String, String> map = new HashMap<>();
        map.put(CommonConstant.FILEINFO.filename.getValue(), fileName);
        map.put(CommonConstant.FILEINFO.fileaddress.getValue(),remotePath);
        map.put(CommonConstant.FILEINFO.filesize.getValue(),String.valueOf(fileSize));
        map.put(CommonConstant.FILEINFO.uploadtype.getValue(), type);
        map.put(CommonConstant.FILEINFO.uploadtime.getValue(),String.valueOf(System.currentTimeMillis()));
        map.put(CommonConstant.FILEINFO.storetype.getValue(),CommonConstant.STORE_TYPE_ALL);
        return map;
    }
}
