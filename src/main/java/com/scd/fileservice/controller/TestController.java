package com.scd.fileservice.controller;

import com.scd.filesdk.engine.BaseEngine;
import com.scd.filesdk.conversion.FileEngineConversion;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chengdu
 */
@RestController
public class TestController {

    @RequestMapping(value = "/engine/{type}",method = RequestMethod.GET)
    public String testEngine(@PathVariable(value = "type") String type){
        BaseEngine baseEngine = FileEngineConversion.convertFileEngine(type);
        return baseEngine.upload("");
    }
}
