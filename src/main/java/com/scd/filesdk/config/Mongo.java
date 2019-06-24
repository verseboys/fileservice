package com.scd.filesdk.config;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;

/**
 * @author chengdu
 * @date 2019/6/25.
 */
@Configuration(value = "mongoconfig")
public class Mongo {

    @Autowired
	private MongoDbFactory mongoDbFactory;

    @Bean
    public GridFSBucket createGridFsBucket(){
        MongoDatabase mongoDatabase = mongoDbFactory.getDb();
        return GridFSBuckets.create(mongoDatabase);
    }
}
