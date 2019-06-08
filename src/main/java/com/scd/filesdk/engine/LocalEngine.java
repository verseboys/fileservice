package com.scd.filesdk.engine;

import com.scd.filesdk.config.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author chengdu
 */
@Component
public class LocalEngine extends BaseEngine{

    @Autowired
    private Local local;

    @Override
    public String upload(String filePath) {
        return "I am Local";
    }
}
