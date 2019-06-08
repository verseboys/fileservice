package com.scd.filesdk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author chengdu
 */
@Component
@ConfigurationProperties(prefix = "file.engine.local")
public class Local {
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Local{" +
                "path='" + path + '\'' +
                '}';
    }

    private String path;
}
