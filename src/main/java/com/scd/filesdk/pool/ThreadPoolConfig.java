package com.scd.filesdk.pool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author chengdu
 * @date 2019/6/28.
 */
@Configuration
public class ThreadPoolConfig {


    @Value("${pool.file.corePoolSize}")
    private int corePoolSize;

    @Value("${pool.file.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${pool.file.keepAliveTime}")
    private long keepAliveTime;

    @Value("${pool.file.timeUnit}")
    private TimeUnit timeUnit;

    @Value("${pool.file.blockingQueue}")
    private int blockingQueue;



    @Bean(name = "fileThreadPool")
    public ExecutorService createThreadPool(){
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, timeUnit, new ArrayBlockingQueue(blockingQueue),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
