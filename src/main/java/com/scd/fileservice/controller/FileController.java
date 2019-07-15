package com.scd.fileservice.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.scd.filesdk.common.PoolType;
import com.scd.filesdk.engine.BaseEngine;
import com.scd.filesdk.model.param.UploadParam;
import com.scd.filesdk.tools.EngineMapperTool;
import com.scd.filesdk.model.param.BreakParam;
import com.scd.filesdk.tools.SingletonPoolTool;
import com.scd.fileservice.data.FileRedisData;
import com.scd.fileservice.model.vo.BreakStatus;
import com.scd.fileservice.model.vo.Result;
import com.scd.fileservice.service.FileService;
import com.scd.fileservice.utils.FileDownLoadUtil;
import com.scd.fileservice.utils.ResultUtil;
import io.swagger.annotations.Api;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.*;

/**
 * @author chengdu
 */
@RestController
@Api(tags = "File Upload Download")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRedisData fileRedisData;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @RequestMapping(value = "/test/engine/{type}",method = RequestMethod.POST)
    public String testEngine(@PathVariable(value = "type") String type, @RequestParam("file") MultipartFile multipartFile) throws Exception{
        BaseEngine baseEngine = EngineMapperTool.getFileEngine(type);
        String originName = multipartFile.getOriginalFilename();
        // inputstream
        InputStream inputStream = multipartFile.getInputStream();
        return baseEngine.upload(inputStream, originName);
        // byte
//        byte[] fbyte = multipartFile.getBytes();
//        return baseEngine.upload(fbyte, originName);
    }

    @RequestMapping(value = "/engine/{type}",method = RequestMethod.POST)
    public Result<String> fileUpload(@PathVariable(value = "type") String type,
                                     @RequestParam("file") MultipartFile multipartFile,
                                     UploadParam uploadParam){
        String checkResult = checkUploadParam(multipartFile, uploadParam);
        if(checkResult.length() > 0){
            return ResultUtil.errorWithOutData(checkResult);
        }
        try {
            String remotePath = fileService.upload(multipartFile, type, uploadParam);
            return ResultUtil.success(remotePath);
        }catch (Exception e){
            LOGGER.error("upload to {} error", type, e);
        }
        return ResultUtil.errorWithOutData("upload to "+type + " error");
    }

    private String checkUploadParam(MultipartFile multipartFile, UploadParam uploadParam) {
        StringBuilder stringBuilder = new StringBuilder("");
        if(multipartFile == null){
            stringBuilder.append("multipartFile is null").append("\n");
        }
        if(StringUtils.isEmpty(multipartFile.getOriginalFilename())){
            stringBuilder.append("originalFilename is empty").append("\n");
        }
        if(uploadParam == null){
            uploadParam = new UploadParam();
        }
        // 兼容老的请求参数，可以不用传 fileName
        if(StringUtils.isEmpty(uploadParam.getFileName())){
            uploadParam.setFileName(multipartFile.getOriginalFilename());
        }
        return stringBuilder.toString();
    }

    @RequestMapping(value = "/breakpoint/checkfilestatus", method = RequestMethod.POST)
    public Result<BreakStatus> checkBreakUploadStatus(@RequestParam(value = "md5") String md5){
        BreakStatus breakStatus = fileService.checkBreakUploadStatus(md5);
        return ResultUtil.success(breakStatus);
    }

    @RequestMapping(value = "/breakpoint/upload/{type}", method = RequestMethod.POST)
    public Result<String> breakUpload(@PathVariable(value = "type") String type, BreakParam breakParam){
        String result = fileService.breakUpload(breakParam, type);
        return ResultUtil.success(result);
    }

    @RequestMapping(value = "/download/{fileid}", method = RequestMethod.GET)
    public Result<String> fileDownLoad(@PathVariable(value = "fileid") String fileId, HttpServletResponse response){
        String result;
        try{
            result = fileService.download(fileId, response);
            if(!StringUtils.isEmpty(result)){
                return ResultUtil.errorWithOutData(result);
            }
        }catch (Exception e){
            LOGGER.error("download file error", e);
        }
        return null;
    }

    @RequestMapping(value = "/list/files", method = RequestMethod.GET)
    public Result<List<Object>> listFiles(){
        return ResultUtil.success(fileRedisData.findAllFileInfo());
    }

    /**
     * 清空所有数据
     * @return
     */
    @RequestMapping(value = "/delete/all", method = RequestMethod.GET)
    public Map<String, Object> deleteAllData(){
        Set<String> keys = stringRedisTemplate.keys("*");
        Long deleted = stringRedisTemplate.delete(keys);
        Map<String, Object> map = new HashMap<>();
        map.put("keys", keys);
        map.put("deleted", deleted);
        return map;
    }

    @RequestMapping(value = "/mongo/test", method = RequestMethod.POST)
    public String monGoTest(@RequestParam("file") MultipartFile multipartFile){
        try {
            InputStream inputStream = multipartFile.getInputStream();
            String fileName = multipartFile.getOriginalFilename();
//            ObjectId objectId = gridFsTemplate.store(inputStream, fileName);
            ObjectId objectId = gridFSBucket.uploadFromStream(fileName, inputStream);
            return objectId.toHexString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping(value = "/mongo/down", method = RequestMethod.GET)
    public void monGoDown(String fileId, HttpServletResponse response){
        try {
            // String 如何转换未 ObjectId ???
//            Query query = Query.query(Criteria.where("_id").is(fileId));
            // 查询单个文件
//            GridFSFile gfsfile = gridFsTemplate.findOne(query);
            ObjectId objectId = new ObjectId(fileId);
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(objectId);
            GridFSFile gridFSFile = gridFSDownloadStream.getGridFSFile();
            FileDownLoadUtil.outputFile(response, gridFSDownloadStream, gridFSFile.getFilename());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/pool/info", method = RequestMethod.GET)
    public Map<String,Object> getPoolInfo(PoolType poolType){
        return  SingletonPoolTool.getPoolInfo(poolType);
    }
}
