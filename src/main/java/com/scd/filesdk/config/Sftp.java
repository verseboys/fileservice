package com.scd.filesdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author chengdu
 */
@Component
@ConfigurationProperties(prefix = "file.engine.sftp")
@Data
public class Sftp {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String path;
}
