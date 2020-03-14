package com.scd.filesdk.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author James
 */
@Configuration(value = "mongoconfig")
@ConditionalOnProperty(name = "file.engine.mongo.enable", havingValue = "true")
public class MongoConfig extends AbstractMongoConfiguration {

    @Autowired
    @Qualifier(value = "mongoproperties")
    private Mongo mongo;

    @Autowired
    private MongoDbFactory mongoDbFactory;

    @Bean
    public GridFSBucket createGridFsBucket(){
        MongoDatabase mongoDatabase = mongoDbFactory.getDb();
        return GridFSBuckets.create(mongoDatabase);
    }

    @Override
    public MongoClient mongoClient() {
        ServerAddress address = new ServerAddress(mongo.getHost(),
                mongo.getPort());
        List<MongoCredential> credentialsList = new ArrayList<>();
        credentialsList.add(MongoCredential.createScramSha1Credential(
                mongo.getUsername(), mongo.getDatabase(),
                mongo.getPassword().toCharArray()));
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        builder.connectionsPerHost(mongo.getConnectionsPerHost());
        builder.connectTimeout(mongo.getConnectTimeout());
        builder.maxWaitTime(mongo.getMaxWaitTime());
        builder.socketTimeout(mongo.getSocketTimeout());
        builder.minConnectionsPerHost(mongo.getMinConnectionsPerHost());
        return new MongoClient(address, credentialsList.get(0), builder.build());
    }

    @Override
    protected String getDatabaseName() {
        return mongo.getDatabase();
    }
}
