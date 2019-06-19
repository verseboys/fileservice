package com.scd.filesdk.conversion;

import com.scd.filesdk.config.InitConfig;
import com.scd.filesdk.engine.BaseEngine;
import com.scd.filesdk.exception.NoSuchEngineExcetion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author chengdu
 */
@Component
public class FileEngineConversion {

    private static InitConfig initConfig;

    @Autowired
    public void setInitConfig(InitConfig config){
        initConfig = config;
    }

    public static BaseEngine convertFileEngine(String engineType){
        Map<String, BaseEngine> engineMap = initConfig.getEngineMap();
        if(!engineMap.containsKey(engineType)){
            throw new NoSuchEngineExcetion("no such engine " + engineType +
                    ", please check config");
        }
        return engineMap.get(engineType);
    }
}