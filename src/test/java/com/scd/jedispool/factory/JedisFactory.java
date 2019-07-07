package com.scd.jedispool.factory;

import com.scd.filesdk.common.ServiceInfo;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * @author chengdu
 * @date 2019/7/1.
 */
public class JedisFactory extends BasePooledObjectFactory<Jedis> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisFactory.class);


    /**
     * borrowObject
     * @return
     * @throws Exception
     */
    @Override
    public Jedis create() throws Exception {
        LOGGER.info("create jedis ~");
        Jedis jedis = new Jedis(ServiceInfo.REDIS.host, ServiceInfo.REDIS.port);
//        jedis.auth(ServiceInfo.REDIS.password);
//        LOGGER.info("jedis connect status {}", jedis.isConnected());
        return jedis;
    }

    @Override
    public void destroyObject(PooledObject<Jedis> p){
        p.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<Jedis> p){
        LOGGER.info("validate Object");
        if(p.getObject().isConnected()){
            return true;
        }else {
            return false;
        }
    }


    @Override
    public void activateObject(PooledObject<Jedis> p) throws Exception {
        LOGGER.info("activate Object");
        Jedis jedis = p.getObject();
        if(jedis != null && !jedis.isConnected()){
            jedis.auth(ServiceInfo.REDIS.password);
            jedis.connect();
        }
    }

    /**
     * returnObject
     * @param p
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<Jedis> p) throws Exception {
        LOGGER.info("passivate Object");
    }

    /**
     * borrowObject
     * @param jedis
     * @return
     */
    @Override
    public PooledObject<Jedis> wrap(Jedis jedis) {
        return new DefaultPooledObject<>(jedis);
    }
}
