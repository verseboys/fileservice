package com.scd.filesdk.util;

import com.scd.filesdk.common.ServiceInfo;
import com.scd.fileservice.common.CommonConstant;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author chengdu
 * @date 2019/6/23.
 */
public class JedisUtil {

    private static Jedis jedis;

    private static final Logger logger = Logger.getLogger("lavasoft");

    public static void init(String host, int port, String password, int databaseIndex){
        logger.info("=========初始化Redis===========:"+host+":"+port);
        jedis = new Jedis(host,port);
        jedis.auth(password);
        jedis.select(databaseIndex);
        logger.info(jedis.ping());
    }

    public static void initWithoutPwd(String host, int port, int databaseIndex){
        logger.info("=========初始化Redis===========:"+host+":"+port);
        jedis = new Jedis(host,port);
        jedis.select(databaseIndex);
        logger.info(jedis.ping());
    }

    public static void quitJedis(){
        if(jedis.isConnected()){
            jedis.quit();
        }
    }

    public static void showAllKeys(){
        Set<String> keys = jedis.keys("*");
        Iterator<String> it=keys.iterator() ;
        while(it.hasNext()){
            String key = it.next();
            String type = jedis.type(key);
            if(type.equals("string")){
                String value = jedis.get(key);
                System.out.println(type + "-->" + key + "-->" + value);
            }else if(type.equals("hash")){
                Map<String, String> map = jedis.hgetAll(key);
                System.out.println(type + "-->" + key + "-->" + map);
            }else if(type.equals("list")){
                Long lenlist = jedis.llen(key);
                List<String> liststr = jedis.lrange(key, 0, lenlist);
                System.out.println(type + "-->" +key + "-->" + liststr);
            }else if(type.equals("set")){
                Set<String> setStrs = jedis.smembers(key);
                System.out.println(type + "-->" +key + "-->" + setStrs);
            }
        }
    }

    public static void selectDatabase(int databaseIndex){
        logger.info("----------RedisDataBase---"+databaseIndex);
        jedis.select(databaseIndex);
    }

    public static Jedis getJedis(){
        if(jedis == null){
            throw new RuntimeException("JRedis not init");
        }
        return jedis;
    }

    public static void initUploadRecord(String fileId, int chunks){
        String rediskey = String.format(CommonConstant.FILE_BREAK_RECORD, fileId);
        StringBuffer stringBuffer = new StringBuffer("");
        for(int i = 0; i < chunks; i++){
            stringBuffer.append("0");
        }
        jedis.set(rediskey, stringBuffer.toString());
    }

    public static void setUploadRecord(String fileId, int chunk){
        String rediskey = String.format(CommonConstant.FILE_BREAK_RECORD, fileId);
        jedis.setrange(rediskey, chunk, "1");
    }

    public static String findUploadRecord(String fileId){
        String rediskey = String.format(CommonConstant.FILE_BREAK_RECORD, fileId);
        return jedis.get(rediskey);
    }


    public static void main(String[] args) throws Exception{
        init(ServiceInfo.REDIS.host, ServiceInfo.REDIS.port, ServiceInfo.REDIS.password,ServiceInfo.REDIS.database);
//        showAllKeys();
        String key = "file:breakaddress:36dea236c9c94abab2c8da23a8f113ce";
        Long size = jedis.llen(key);
        List<String> list1 = jedis.lrange(key, 0, size);
        List<String> list2 = jedis.lrange(key, 0, size - 1);
        System.out.println(list1);
    }
}
