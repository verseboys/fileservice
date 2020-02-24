package com.scd.filesdk.config;

import com.scd.filesdk.common.ServiceInfo;
import com.scd.filesdk.engine.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengdu
 */
@Component
public class InitConfig implements InitializingBean{

//    @Autowired
//    private LocalEngine localEngine;
//
//    @Autowired
//    private SftpEngine sftpEngine;
//
//    @Autowired
//    private FtpEngine ftpEngine;
//
//    @Autowired
//    private MongoEngine mongoEngine;
//
//    @Autowired
//    private FdfsEngine fdfsEngine;

    private List<Class<BaseEngine>> beanClassList = Arrays.asList(
            new Class[]{LocalEngine.class, SftpEngine.class,
                    FtpEngine.class, MongoEngine.class,
                    FdfsEngine.class});

    private Map<Class<? extends BaseEngine>, String> ngMap = new HashMap<Class<? extends BaseEngine>, String>(){
        {
            put(LocalEngine.class, ServiceInfo.ENGINE.LOCAL);
            put(SftpEngine.class, ServiceInfo.ENGINE.SFTP);
            put(FtpEngine.class, ServiceInfo.ENGINE.FTP);
            put(MongoEngine.class, ServiceInfo.ENGINE.MONGO);
            put(FdfsEngine.class, ServiceInfo.ENGINE.FDSF);
        }
    };

    public Map<String, BaseEngine> getEngineMap() {
        return engineMap;
    }


    private Map<String, BaseEngine> engineMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        beanClassList.forEach(baseEngineClass -> {
            BaseEngine baseEngine = findConfigBean(baseEngineClass);
            if (baseEngine != null) {
                String ngStr = ngMap.get(baseEngineClass);
                if (ngStr != null) {
                    engineMap.put(ngStr, baseEngine);
                }
            }
        });
    }

    public <T>  T findConfigBean(Class<T> clazz) {
        try {
            return BeanPool.getBean(clazz);
        } catch (Exception e) {
        }
        return null;
    }
}
