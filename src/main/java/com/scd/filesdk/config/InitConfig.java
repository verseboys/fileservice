package com.scd.filesdk.config;

import com.scd.filesdk.engine.BaseEngine;
import com.scd.filesdk.engine.LocalEngine;
import com.scd.filesdk.engine.SftpEngine;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengdu
 */
@Component
public class InitConfig implements InitializingBean{

    @Autowired
    private LocalEngine localEngine;

    @Autowired
    private SftpEngine sftpEngine;

    public Map<String, BaseEngine> getEngineMap() {
        return engineMap;
    }

    public void setEngineMap(Map<String, BaseEngine> engineMap) {
        this.engineMap = engineMap;
    }

    private Map<String, BaseEngine> engineMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        engineMap.put("local",localEngine);
        engineMap.put("sftp", sftpEngine);
    }
}
