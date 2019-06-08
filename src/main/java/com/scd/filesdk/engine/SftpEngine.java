package com.scd.filesdk.engine;

import com.scd.filesdk.config.Sftp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author chengdu
 */
@Component
public class SftpEngine extends BaseEngine{

    @Autowired
    private Sftp sftp;

    @Override
    public String upload(String filePath) {
        return "I am SftpEngine";
    }
}
