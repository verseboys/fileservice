package com.scd.fileservice.controller;

import com.scd.filesdk.engine.BaseEngine;
import com.scd.filesdk.conversion.FileEngineConversion;
import com.scd.filesdk.util.FileUtil;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author chengdu
 */
@RestController
public class TestController {

    @RequestMapping(value = "/engine/{type}",method = RequestMethod.POST)
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
}
