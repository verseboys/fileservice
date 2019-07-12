package com.scd.fileservice.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.scd.fileservice.model.vo.Result;
import com.scd.fileservice.utils.ResultUtil;
import io.swagger.annotations.Api;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author chengdu
 * @date 2019/7/12.
 */
@RestController
@Api(tags = "Logger Controller")
public class LoggerController {

    private enum LoggerLevel {
        DEBUG,INFO,WARN,ERROR
    }


    @RequestMapping(value="/logger/level", method = RequestMethod.POST)
    public Result<Level> updateLogLevel(LoggerLevel loggerLevel, String packageName){
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        if(StringUtils.isEmpty(packageName)){
            packageName = "root";
        }
        loggerContext.getLogger(packageName).setLevel(Level.toLevel(loggerLevel.name()));
        return ResultUtil.success(getLoggerLevel(loggerContext, packageName));
    }

    @RequestMapping(value="/logger/level", method = RequestMethod.GET)
    public Result<Level> getLoggerLevel(String packageName){
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        if(StringUtils.isEmpty(packageName)){
            packageName = "root";
        }
        return ResultUtil.success(getLoggerLevel(loggerContext, packageName));
    }


    private Level getLoggerLevel(LoggerContext loggerContext, String packageName){
        return loggerContext.getLogger(packageName).getLevel();
    }
}
