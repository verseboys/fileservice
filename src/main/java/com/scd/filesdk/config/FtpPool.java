package com.scd.filesdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author chengdu
 * @date 2019/7/2.
 */
@Component
@ConfigurationProperties(prefix = "pool.ftp")
@Data
public class FtpPool {

    private int maxTotal=100;
    private int maxIdle=100;
    private long maxWait=3000;
    private boolean testWhileIdle=true;
    private long timeBetweenEvictionRunsMillis=1000 * 60;
}
