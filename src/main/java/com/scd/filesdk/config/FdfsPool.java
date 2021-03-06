package com.scd.filesdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author chengdu
 * @date 2019/7/14.
 */
@Component
@ConfigurationProperties(prefix = "pool.fdfs")
@Data
public class FdfsPool {

    private int maxTotal=100;
    private int maxIdle=100;
    private long maxWait=3000;
    private boolean testWhileIdle=true;
    private boolean testOnOnBorrow = true;
    private long timeBetweenEvictionRunsMillis=1000 * 60;
}
