package com.scd.filesdk.common;

/**
 * 存储一些常量
 * @author chengdu
 * @date 2019/6/20.
 */
public interface ServiceInfo {

    interface ENGINE {
        String LOCAL = "local";
        String SFTP = "sftp";
        String FTP = "ftp";
        String MONGO = "mongo";
    }

    interface SFTP {
        String host = "192.168.1.101";
        int port = 22;
        String username = "test";
        String password = "testT2018";
    }

    interface FTP {
        String host = "192.168.1.101";
        int port = 21;
        String username = "test";
        String password = "testT2018";
    }

    // 七牛云
    interface QN {
        String accessKey = "";
        String secretKey = "";
        String bucket = "chengdu";
    }

    interface REDIS {
        String host = "127.0.0.1";
        int port = 6379;
        String password = "test";
        int database = 15;
    }
}
