package com.scd.thread.apachepool;

import com.scd.thread.conn.SftpConn;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chengdu
 * @date 2019/6/30.
 */
public class ConnectionFactory extends BasePooledObjectFactory<SftpConn> {

    private AtomicInteger idCount = new AtomicInteger(1);

    @Override
    public SftpConn create() throws Exception {
        int count = idCount.getAndAdd(1);
        return new SftpConn(count);
    }

    @Override
    public PooledObject<SftpConn> wrap(SftpConn sftpConn) {
        return new DefaultPooledObject<>(sftpConn);
    }
}
