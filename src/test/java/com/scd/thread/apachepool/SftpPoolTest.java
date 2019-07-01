package com.scd.thread.apachepool;

import com.scd.thread.conn.SftpConn;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chengdu
 * @date 2019/6/30.
 */
public class SftpPoolTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(SftpPoolTest.class);

    @Test
    public void testPoolBroww() throws Exception {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        ConnectionFactory connectionFactory = new ConnectionFactory();
        config.setMaxTotal(10);
        config.setMaxWaitMillis(2000);
        SftpConnectionPool sftpConnectionPool = new SftpConnectionPool(connectionFactory, config);
        for(int i=0; i < 11; i++){
            SftpConn sftpConn = sftpConnectionPool.borrowObject();
            LOGGER.info("current conn {}, active conn {}", sftpConn, sftpConnectionPool.getNumActive());
//            sftpConnectionPool.returnObject(sftpConn);
        }
    }
}
