package com.example.demo.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Configurable
public class MongodbConfig {

    @Bean
    public MongoTransactionManager initMongoTransactionManager(MongoDbFactory factory) {
        return new MongoTransactionManager(factory);
    }

}

