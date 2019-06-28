package com.scd.filesdk.pool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chengdu
 * @date 2019/6/28.
 */
@Configuration
public class ThreadPoolConfig {


    @Value("${download.pool.threads}")
    private int threads;

    @Bean(name = "fileThreadPool")
    public ExecutorService createThreadPool()
    {
        return Executors.newFixedThreadPool(threads);
    }
}
