package com.scd.fileservice.service.impl;

import com.scd.filesdk.common.ServiceInfo;
import com.scd.filesdk.model.param.UploadParam;
import com.scd.filesdk.tools.EngineMapperTool;
import com.scd.filesdk.engine.BaseEngine;
import com.scd.filesdk.model.param.BreakParam;
import com.scd.filesdk.model.vo.BreakResult;
import com.scd.filesdk.util.FileUtil;
import com.scd.fileservice.common.CommonConstant;
import com.scd.fileservice.data.FileRedisData;
import com.scd.fileservice.model.vo.BreakStatus;
import com.scd.fileservice.model.vo.BreakFileInfo;
import com.scd.fileservice.model.vo.DownParam;
import com.scd.fileservice.service.FileService;
import com.scd.fileservice.utils.FileDownLoadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * @author chengdu
 * @date 2019/6/22.
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private FileRedisData fileRedisData;

    @Autowired
    @Qualifier(value = "fileThreadPool")
    private ExecutorService fileThreadPool;

    @Value("${temp.mergebreakpath}")
    private String downTemp;

    @Override
    public String upload(MultipartFile multipartFile, String type, UploadParam uploadParam) throws Exception {
        InputStream inputStream = multipartFile.getInputStream();
        BaseEngine baseEngine = EngineMapperTool.getFileEngine(type);
        String remotePath = baseEngine.upload(inputStream, uploadParam);
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
        Object storetype = fileInfoMap.get(CommonConstant.FILEINFO.storetype.getValue());
        if(StringUtils.isEmpty(uploadtype) || StringUtils.isEmpty(fileName)
                || StringUtils.isEmpty(storetype)){
            return "data exception";
        }
        // 存储类型为所有的时候, 直接下载
        if(CommonConstant.STORE_TYPE_ALL.equals(storetype)) {
            if(StringUtils.isEmpty(remotePath)){
                return "can not find remote path";
            }
            BaseEngine baseEngine = EngineMapperTool.getFileEngine(uploadtype.toString());
            // 下载文件
            InputStream inputStream = baseEngine.download(remotePath.toString());
            // 输出文件
            FileDownLoadUtil.outputFile(response, inputStream, fileName.toString());
        }else{
            // 查询断点信息
//            Map<Object, Object> breaFilekMap = fileRedisData.findBreakFileInfo(fileId);
//            Object status = breaFilekMap.get("file_status");
//            if( ! CommonConstant.STR_TRUE.equals(status)){
//                return "data exception";
//            }
//            Object chunkNum = breaFilekMap.get("chunk_num");
//            Object chunkSizeObj = breaFilekMap.get("chunk_size");
//            int chunks = Integer.valueOf(chunkNum.toString());
//            List<String> fileAddress = fileRedisData.findBreakAddress(fileId, chunks);
//            long chunkSize = Long.valueOf(chunkSizeObj.toString());
//            // 创建临时文件目录
//            String tempPath = downTemp + File.separator + System.currentTimeMillis() + File.separator + fileName;
//            File tempFile = FileUtil.createFile(tempPath);
//            if(tempFile.exists() && tempFile.isFile()){
//                tempFile.delete();
//            }
//            List<Future<BreakMergeResult>> futureList = new ArrayList<>(fileAddress.size());
//            for(String address : fileAddress){
//                int index = address.indexOf("_");
//                String chunkStr = address.substring(0,index);
//                String fileaddress = address.substring(index + 1);
//                BreakTask breakTask = new BreakTask(Integer.valueOf(chunkStr),chunkSize,
//                        uploadtype.toString(), fileaddress, tempFile);
//                Future<BreakMergeResult> future = fileThreadPool.submit(breakTask);
//                futureList.add(future);
//            }
//            for(Future<BreakMergeResult> future : futureList){
//                try {
//                    BreakMergeResult breakMergeResult = future.get();
//                    LOGGER.info("merge file task result {}", breakMergeResult);
//                }catch (Exception e){
//                    LOGGER.error("get merge file task error");
//                }
//            }
//            InputStream inputStream = new FileInputStream(tempFile);
//            // 输出文件
//            FileDownLoadUtil.outputFile(response, inputStream, fileName.toString());
              return "use client to download break file, wating .....";
        }
        return "";
    }

    @Override
    public String breakUpload(BreakParam breakParam, String type) {
        BaseEngine baseEngine = EngineMapperTool.getFileEngine(type);
        BreakResult breakResult = baseEngine.upload(breakParam);
        if(!breakResult.isWriteSuccess()){
            return "upload to " + type + " error";
        }
        String md5 = breakParam.getMd5();
        String result = CommonConstant.RESULT_PART_UPLOAD;
        // save chunk upload info to db
        saveChunkInfo(breakParam, breakResult, type);
        if(checkAllPartUpload(breakParam, breakResult, type)){
            String fileName = breakParam.getName();
            if(ServiceInfo.ENGINE.LOCAL.equals(type)){
                File tempFile = breakResult.getTempFile();
                boolean flag = FileUtil.renameFile(tempFile, fileName);
                if(flag){
                    saveBreak(breakParam, breakResult, type);
                }else{
                    LOGGER.error("upload to {} ,save break temp file error", type);
                }
            }else{
                saveBreak(breakParam, breakResult, type);
            }
            // 移除上传块记录、期望上传记录
            deleteUploadRecord(breakParam);
            result = CommonConstant.RESULT_ALL_UPLOAD;
        }
        return breakParam.getChunk() + "_" + md5 + "_" + result;
    }

    @Override
    public BreakStatus checkBreakUploadStatus(String md5) {
        Map<Object,Object> map = fileRedisData.findBreakFileInfo(md5);
        if(map == null || map.size() == 0){
            return new BreakStatus(CommonConstant.FILESTATUS.NOT.getStatus());
        }
        Object fileStatus = map.get(CommonConstant.BREAKINFO.filestatus.getValue());
        Object chunkSize = map.get(CommonConstant.BREAKINFO.chunksize.getValue());
        // 如果为true 说明已上传完成
        if(CommonConstant.STR_TRUE.equals(String.valueOf(fileStatus))){
            return new BreakStatus(CommonConstant.FILESTATUS.ALL.getStatus());
        }else{
            // 查询break_record 查找那些块未上传
            String record = fileRedisData.findBreakRecord(md5);
            byte[] recordArr = record.getBytes();
            List<Integer> notUpload = new ArrayList<>(10);
            for(int i = 0; i < recordArr.length; i++){
                if(recordArr[i] == 48){
                    notUpload.add(i);
                }
            }
            Long chunkSizeL = 0L;
            if(chunkSize != null) {
              chunkSizeL = Long.valueOf(chunkSize.toString());
            }
            return new BreakStatus(CommonConstant.FILESTATUS.PART.getStatus(), chunkSizeL, notUpload);
        }
    }

    private void deleteUploadRecord(BreakParam breakParam) {
        fileRedisData.deleteBreakRecord(breakParam.getMd5());
        fileRedisData.deleteBreakExpected(breakParam.getMd5());
    }

    private void saveBreak(BreakParam breakParam, BreakResult breakResult, String type) {
         Map<String,String> map = createFileInfoBreak(breakParam,breakResult, type);
         fileRedisData.saveFileInfo(breakParam.getMd5(), map);
         // 更新断点文件状态
         fileRedisData.updateBreakInfoStatus(breakParam.getMd5(), true);
         // 保存文件Id
         fileRedisData.saveFileId(breakParam.getMd5());
    }

    private void saveChunkInfo(BreakParam breakParam, BreakResult breakResult, String type){
        // 断点续传使用文件 md5 作为 fileId
        String md5 = breakParam.getMd5();
        int curChunk = breakParam.getChunk();
        // 如果不是存储本地，记录每一块的存储地址
        if(! ServiceInfo.ENGINE.LOCAL.equals(type)){
            // 连接上第几块
            String dataBasePath = curChunk + "_" + breakResult.getFilePath();
            fileRedisData.saveBreakAddress(md5,dataBasePath);
        }
    }

    private boolean checkAllPartUpload(BreakParam breakParam, BreakResult breakResult, String type) {
        String md5 = breakParam.getMd5();
        long chunkSize = breakParam.getChunkSize();
        int curChunk = breakParam.getChunk();
        int chunks = breakParam.getChunks();
        // 文件第一次上传初始化 块值
        synchronized (FileServiceImpl.class) {
            if (!fileRedisData.existsBreakRecord(md5)) {
                // 存储断点信息
                Map<String, String> breakFileMap = createBreakFileInfo(chunkSize, chunks, false);
                fileRedisData.saveBreakInfo(md5, breakFileMap);
                if (chunks == 0 || chunks == 1) {
                    return true;
                }
                // 初始化上传块记录
                LOGGER.info("first upload {}, init part record", md5);
                fileRedisData.initBreakRecord(md5, chunks);
            }
        }
        // 当前块设置为 1
        fileRedisData.setuploadedChunk(md5, curChunk);
        String breakRecord = fileRedisData.findBreakRecord(md5);
        LOGGER.info("now upload record is {}", breakRecord);
        // 检查那一块未上传
//        for(byte b : breakRecord){
//            if("0".equals(String.valueOf(b))){
//                return false;
//            }
//        }
        String expectedResult = fileRedisData.findBreakExpected(md5);
        // 对比期望结果与记录集是否相同
        if(! expectedResult.equals(breakRecord)){
            return false;
        }
        return true;
    }

    private Map<String,String> createBreakFileInfo(long chunkSize,int chunks, boolean fileStatus) {
        Map<String,String> map = new HashMap<>(2);
        map.put(CommonConstant.BREAKINFO.chunksize.getValue(), String.valueOf(chunkSize));
        map.put(CommonConstant.BREAKINFO.chunknum.getValue(), String.valueOf( chunks == 0 ? 1:chunks ));
        map.put(CommonConstant.BREAKINFO.filestatus.getValue(),String.valueOf(fileStatus));
        return map;
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

    private Map<String,String> createFileInfoBreak(BreakParam breakParam, BreakResult breakResult, String type) {
        String fileName = breakParam.getName();
        long fileSize = breakParam.getSize();
        String remotePath = "";
        String storeType = "";
        // 存储在本地的时候有路径，切片存储去 breakaddress 找地址
        // 下载的时候根据 storeType 区分如何下载
        if(ServiceInfo.ENGINE.LOCAL.equals(type)){
            remotePath = breakResult.getFilePath();
            remotePath = remotePath.substring(0, (remotePath.length() - "_tmp".length()));
            storeType = CommonConstant.STORE_TYPE_ALL;
        }else{
            storeType = CommonConstant.STORE_TYPE_BLOCK;
        }
        Map<String, String> map = new HashMap<>();
        map.put(CommonConstant.FILEINFO.filename.getValue(), fileName);
        map.put(CommonConstant.FILEINFO.fileaddress.getValue(),remotePath);
        map.put(CommonConstant.FILEINFO.filesize.getValue(),String.valueOf(fileSize));
        map.put(CommonConstant.FILEINFO.uploadtype.getValue(), type);
        map.put(CommonConstant.FILEINFO.uploadtime.getValue(),String.valueOf(System.currentTimeMillis()));
        map.put(CommonConstant.FILEINFO.storetype.getValue(),storeType);
        return map;
    }

    public BreakFileInfo checkFile(String fileId){
        String msg = "";
        BreakFileInfo breakFileInfo = new BreakFileInfo();
        if(!fileRedisData.existsFileId(fileId)){
            msg = "file not exists";
            breakFileInfo.setMessage(msg);
        }else{
            breakFileInfo.setExists(true);
            Map<Object, Object> fileInfoMap = fileRedisData.findFileInfo(fileId);
            Object uploadtype = fileInfoMap.get(CommonConstant.FILEINFO.uploadtype.getValue());
            Object storetype = fileInfoMap.get(CommonConstant.FILEINFO.storetype.getValue());
            breakFileInfo.setUploadType(String.valueOf(uploadtype));
            if(! CommonConstant.STORE_TYPE_BLOCK.equals(storetype)){
                msg = "file not block store";
                breakFileInfo.setMessage(msg);
                return breakFileInfo;
            }
            breakFileInfo.setBlock(true);
            Map<Object, Object> breakFileInfoMap = fileRedisData.findBreakFileInfo(fileId);
            Object chunks = breakFileInfoMap.get(CommonConstant.BREAKINFO.chunknum.getValue());
            Object chunkSize = breakFileInfoMap.get(CommonConstant.BREAKINFO.chunksize.getValue());
            breakFileInfo.setChunkSize(Long.valueOf(chunkSize.toString()));
            List<String> fileAddress = fileRedisData.findBreakAddress(fileId, Integer.valueOf(chunks.toString()));
            breakFileInfo.setFileAddress(fileAddress);
        }
        return breakFileInfo;
    }

    public byte[] downloadChunk(DownParam downParam) throws Exception {
        String uploadType = downParam.getUploadType();
        String address = downParam.getFileAddress();
        int index = address.indexOf("_");
        address = address.substring(index + 1);
        BaseEngine baseEngine = EngineMapperTool.getFileEngine(uploadType);
        return baseEngine.downloadByte(address);
    }
}
