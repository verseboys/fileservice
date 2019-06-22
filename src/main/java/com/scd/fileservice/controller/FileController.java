package com.scd.fileservice.controller;

import com.scd.filesdk.engine.BaseEngine;
import com.scd.filesdk.conversion.FileEngineConversion;
import com.scd.fileservice.data.FileRedisData;
import com.scd.fileservice.model.vo.Result;
import com.scd.fileservice.service.FileService;
import com.scd.fileservice.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * @author chengdu
 */
@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRedisData fileRedisData;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @RequestMapping(value = "/test/engine/{type}",method = RequestMethod.POST)
    public String testEngine(@PathVariable(value = "type") String type, @RequestParam("file") MultipartFile multipartFile) throws Exception{
        BaseEngine baseEngine = FileEngineConversion.convertFileEngine(type);
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
                                     @RequestParam("file") MultipartFile multipartFile){
        try {
            String remotePath = fileService.upload(multipartFile, type);
            return ResultUtil.success(remotePath);
        }catch (Exception e){
            LOGGER.error("upload to {} error", type, e);
        }
        return ResultUtil.errorWithOutData("upload to "+type + " error");
    }

    @RequestMapping(value = "/download/{fileid}", method = RequestMethod.GET)
    public Result<String> fileDownLoad(@PathVariable(value = "fileid") String fileId, HttpServletResponse response){
        String result;
        try{
            result = fileService.download(fileId, response);
            if(StringUtils.isEmpty(result)){
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

}
