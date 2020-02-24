package com.scd.filesdk.config;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;

/**
 * @author chengdu
 * @date 2019/6/25.
 */
@Configuration(value = "mongoconfig")
@ConditionalOnProperty(name = "file.engine.mongo.enable", havingValue = "true")
public class Mongo {

    @Autowired
	private MongoDbFactory mongoDbFactory;

    @Bean
    public GridFSBucket createGridFsBucket(){
        MongoDatabase mongoDatabase = mongoDbFactory.getDb();
        return GridFSBuckets.create(mongoDatabase);
    }
}
