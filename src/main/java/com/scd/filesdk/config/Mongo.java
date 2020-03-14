package com.scd.filesdk.config;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

/**
 * @author chengdu
 * @date 2019/6/25.
 */
@Configuration(value = "mongoproperties")
@ConfigurationProperties(prefix = "file.engine.mongo")
@Data
public class Mongo {
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private int connectionsPerHost;
    private int minConnectionsPerHost;
    private int threadsAllowedToBlockForConnectionMultiplier;
    private int connectTimeout;
    private int maxWaitTime;
    private int socketTimeout;

}
