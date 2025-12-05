package com.harsh.monitoring.collector_service.config

import com.mongodb.client.MongoClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory

@Configuration
class MongoConfig {

    @Bean
    fun logsMongoClient(): MongoClient =
        com.mongodb.client.MongoClients.create("mongodb://localhost:27017")

    @Bean
    fun metadataMongoClient(): MongoClient =
        com.mongodb.client.MongoClients.create("mongodb://localhost:27018")

    @Bean
    fun logsDatabaseFactory(): MongoDatabaseFactory =
        SimpleMongoClientDatabaseFactory(logsMongoClient(), "logs-db")

    @Bean
    fun metadataDatabaseFactory(): MongoDatabaseFactory =
        SimpleMongoClientDatabaseFactory(metadataMongoClient(), "metadata-db")

    @Bean
    fun logsMongoTemplate(): MongoTemplate =
        MongoTemplate(logsDatabaseFactory())

    @Bean
    fun metadataMongoTemplate(): MongoTemplate =
        MongoTemplate(metadataDatabaseFactory())
}
