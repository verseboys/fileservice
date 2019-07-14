package com.scd.filesdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author chengdu
 * @date 2019/7/13.
 */
@Component
@ConfigurationProperties(prefix = "file.engine.fdfs")
@Data
public class Fdfs {

    private String config;

    private String group;
}
