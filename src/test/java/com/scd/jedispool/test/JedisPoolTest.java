package com.scd.jedispool.test;

import com.scd.jedispool.factory.JedisFactory;
import com.scd.jedispool.pool.JedisPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.*;

/**
 * @author chengdu
 * @date 2019/7/1.
 */
public class JedisPoolTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisPoolTest.class);

    private GenericObjectPoolConfig jedisPoolConfig = new GenericObjectPoolConfig<>();

    private JedisFactory jedisFactory = new JedisFactory();

    private JedisPool jedisPool;

    @Before
    public void configPool(){
        jedisPoolConfig.setMinIdle(GenericObjectPoolConfig.DEFAULT_MIN_IDLE);
        jedisPoolConfig.setMaxIdle(GenericObjectPoolConfig.DEFAULT_MAX_IDLE);
        jedisPoolConfig.setMaxTotal(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL);
        jedisPoolConfig.setMaxWaitMillis(1000 * 30);
        // 每次 Borrow 都会去 validateObject
//        jedisPoolConfig.setTestOnBorrow(true);
        // 空闲检测
        jedisPoolConfig.setTestWhileIdle(true);
        // 检测时间可以调大点
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(1000 * 5);
        jedisPool = new JedisPool(jedisFactory, jedisPoolConfig);
    }

    @Test
    public void testJedisConn(){
        Jedis jedis = null;
        try {
            jedis = jedisPool.borrowObject();
            assertEquals("PONG", jedis.ping());
            assertTrue(jedis.isConnected());
            LOGGER.info("jedisPool active {} idle {} waiters {}",
                    jedisPool.getNumActive(), jedisPool.getNumIdle(), jedisPool.getNumWaiters());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisPool.returnObject(jedis);
        }
        LOGGER.info("jedisPool active {} idle {} waiters {}",
                jedisPool.getNumActive(), jedisPool.getNumIdle(), jedisPool.getNumWaiters());
    }

    @Test
    public void testActiveIdle(){
        try {
            Jedis jedis = jedisPool.borrowObject();
            assertEquals(1, jedisPool.getNumActive());
            assertEquals(0, jedisPool.getNumIdle());
            Jedis jedis1 = jedisPool.borrowObject();
            assertEquals(2, jedisPool.getNumActive());
            assertEquals(0, jedisPool.getNumIdle());
            jedisPool.returnObject(jedis);
            assertEquals(1, jedisPool.getNumActive());
            assertEquals(1, jedisPool.getNumIdle());
            jedisPool.returnObject(jedis1);
            assertEquals(0, jedisPool.getNumActive());
            assertEquals(2, jedisPool.getNumIdle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMaxActive() throws Exception {
        for(int i = 0; i < GenericObjectPoolConfig.DEFAULT_MAX_TOTAL; i++){
            jedisPool.borrowObject();
        }
        try {
            jedisPool.borrowObject();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 空闲检测
     */
    @Test
    public void testIdleEvict() throws InterruptedException, Exception{
        Jedis[] jedisArr = new Jedis[8];
        for(int i = 0; i < 8; i++){
            jedisArr[i] = jedisPool.borrowObject();
        }
        assertEquals(8, jedisPool.getNumActive());
        for(int i = 0; i < 8; i++){
            jedisPool.returnObject(jedisArr[i]);
        }
        assertEquals(8, jedisPool.getNumIdle());
        Thread.sleep(1000 * 20);
    }
}
