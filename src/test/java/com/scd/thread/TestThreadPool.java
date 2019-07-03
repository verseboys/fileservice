package com.scd.thread;

import com.scd.thread.task.CachedThreadTask;
import com.scd.thread.task.FixedThreadTask;
import com.scd.thread.task.ThreadTask;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author chengdu
 * @date 2019/6/29.
 */
public class TestThreadPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestThreadPool.class);

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(30, 500,
            60L, TimeUnit.SECONDS, new ArrayBlockingQueue(500));

    private static ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(500);

    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public static ExecutorService getNewFixedThreadPool() {
        return newFixedThreadPool;
    }

    public static ExecutorService getCachedThreadPool() {
        return cachedThreadPool;
    }

    @Test
    public void testThreadPoolExecutor() throws InterruptedException {
        LOGGER.info("wating jconsole connect~~~");
        Thread.sleep(20000);
        int taskSize = 1000; // <= maximumPoolSize +  workQueue.size 500 + 500
        List<Future<String>> futureList = new ArrayList<>(taskSize);
        for(int i = 0; i < taskSize; i++){
            ThreadTask threadTask = new ThreadTask("task_"+i);
            Future<String> future = threadPoolExecutor.submit(threadTask);
            futureList.add(future);
        }
        showTaskResult(futureList);
        LOGGER.info("child thread over");
        LOGGER.info("jconsole thread num to corePoolSize");
        Thread.sleep(60000 * 2);
        LOGGER.info("ActiveCount: {} poolSize: {} queueSize: {} taskCount: {}  completed: {}",
                threadPoolExecutor.getActiveCount(), threadPoolExecutor.getPoolSize(), threadPoolExecutor.getQueue().size(),
                threadPoolExecutor.getTaskCount(),threadPoolExecutor.getCompletedTaskCount());
    }

    @Test
    public void testThreadPoolExecutorException() throws Exception {
        LOGGER.info("wating jconsole connect~~~");
        Thread.sleep(20000);
        int taskSize = 10000000;
        List<Future<String>> futureList = new ArrayList<>(taskSize);
        for(int i = 0; i < taskSize; i++){
            ThreadTask threadTask = new ThreadTask("task_"+i);
            try {
                Future<String> future = threadPoolExecutor.submit(threadTask);
                futureList.add(future);
            }catch (Exception e){
//                e.printStackTrace();
                LOGGER.error(e.getMessage());
            }
        }
        showTaskResult(futureList);
        LOGGER.info("child thread over");
        LOGGER.info("jconsole thread num to corePoolSize");
        Thread.sleep(60000 * 2);
        LOGGER.info("ActiveCount: {} poolSize: {} queueSize: {} taskCount: {}  completed: {}",
                threadPoolExecutor.getActiveCount(), threadPoolExecutor.getPoolSize(), threadPoolExecutor.getQueue().size(),
                threadPoolExecutor.getTaskCount(),threadPoolExecutor.getCompletedTaskCount());
    }

    /**
     * 任务数量过大时，LinkedBlockingQueue 堆积大量请求
     * 导致内存飙升
     * @throws Exception
     */
    @Test
    public void testnewFixedThreadPool() throws Exception {
        LOGGER.info("wating jconsole connect~~~");
        Thread.sleep(20000);
        int taskSize = 10000000;
        List<Future<String>> futureList = new ArrayList<>(taskSize);
        for(int i = 0; i < taskSize; i++){
            FixedThreadTask threadTask = new FixedThreadTask("task_"+i);
            try {
                Future<String> future = newFixedThreadPool.submit(threadTask);
                futureList.add(future);
            }catch (Exception e){
//                e.printStackTrace();
                LOGGER.error(e.getMessage());
            }
        }
        showTaskResult(futureList);
        LOGGER.info("child thread over");
        LOGGER.info("jconsole thread num to corePoolSize");
        Thread.sleep(60000 * 2);
    }

    /**
     * 创建大量线程，系统崩溃
     * @throws Exception
     */
    @Test
    public void newCachedThreadPool() throws Exception {
        LOGGER.info("wating jconsole connect~~~");
        Thread.sleep(20000);
        int taskSize = 10000000;
        List<Future<String>> futureList = new ArrayList<>(taskSize);
        for(int i = 0; i < taskSize; i++){
            CachedThreadTask threadTask = new CachedThreadTask("task_"+i);
            try {
                Future<String> future = cachedThreadPool.submit(threadTask);
                futureList.add(future);
            }catch (Exception e){
//                e.printStackTrace();
                LOGGER.error(e.getMessage());
            }
        }
        showTaskResult(futureList);
        LOGGER.info("child thread over");
        LOGGER.info("jconsole thread num to corePoolSize");
        Thread.sleep(60000 * 2);
    }

    private void showTaskResult(List<Future<String>> futureList){
        for(Future<String> future : futureList){
            try {
                LOGGER.info("task result {}", future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}
