package com.scd.thread.task;

import com.scd.thread.TestThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author chengdu
 * @date 2019/6/30.
 */
public class ThreadTask implements Callable<String> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ThreadTask.class);

    private String name;

    public ThreadTask(String name){
        this.name = name;
    }

    @Override
    public String call() throws Exception {
//        LOGGER.info("thread {}, task {}, start time {}", Thread.currentThread().getName(), name,
//                System.currentTimeMillis());
        ThreadPoolExecutor pool = TestThreadPool.getThreadPoolExecutor();
        LOGGER.info("ActiveCount: {} poolSize: {} queueSize: {} taskCount: {}  completed: {}",
                pool.getActiveCount(), pool.getPoolSize(), pool.getQueue().size(),
                pool.getTaskCount(),pool.getCompletedTaskCount());
        Thread.sleep(10000);
//        LOGGER.info("thread {}, task {}, end time {}", Thread.currentThread().getName(), name,
//                System.currentTimeMillis());
        return "SUCCESS";
    }
}
