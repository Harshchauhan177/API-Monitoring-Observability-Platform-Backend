package com.harsh.monitoring.collector_service.config

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory

@Configuration
class MongoConfig {

    // ----- LOGS DB -----
    @Bean(name = ["logsMongoClient"])
    @Primary  // <----- This makes logs DB the default when Spring is confused
    fun logsMongoClient(): MongoClient =
        MongoClients.create("mongodb://localhost:27017")

    @Bean(name = ["logsDatabaseFactory"])
    @Primary  // <----- Also mark the logs MongoDatabaseFactory as primary
    fun logsDatabaseFactory(): MongoDatabaseFactory =
        SimpleMongoClientDatabaseFactory(logsMongoClient(), "logs-db")

    @Bean(name = ["logsMongoTemplate"])
    fun logsMongoTemplate(): MongoTemplate =
        MongoTemplate(logsDatabaseFactory())

    @Bean(name = ["logsTransactionManager"])
    @Primary
    fun logsTransactionManager(): MongoTransactionManager =
        MongoTransactionManager(logsDatabaseFactory())


    // ----- METADATA DB -----
    @Bean(name = ["metadataMongoClient"])
    fun metadataMongoClient(): MongoClient =
        MongoClients.create("mongodb://localhost:27018")

    @Bean(name = ["metadataDatabaseFactory"])
    fun metadataDatabaseFactory(): MongoDatabaseFactory =
        SimpleMongoClientDatabaseFactory(metadataMongoClient(), "metadata-db")

    @Bean(name = ["metadataMongoTemplate"])
    fun metadataMongoTemplate(): MongoTemplate =
        MongoTemplate(metadataDatabaseFactory())

    @Bean(name = ["metadataTransactionManager"])
    fun metadataTransactionManager(): MongoTransactionManager =
        MongoTransactionManager(metadataDatabaseFactory())
}
