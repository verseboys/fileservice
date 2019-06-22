package com.scd.filesdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author chengdu
 */
@Component
@ConfigurationProperties(prefix = "file.engine.local")
@Data
public class Local {
    private String path;
}
