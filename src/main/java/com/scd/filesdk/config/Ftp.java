package com.scd.filesdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author chengdu
 * @date 2019/6/20.
 */
@Component
@ConfigurationProperties(prefix = "file.engine.ftp")
@Data
public class Ftp {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String path;
}
