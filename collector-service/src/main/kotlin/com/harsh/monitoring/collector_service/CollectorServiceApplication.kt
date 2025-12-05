package com.harsh.monitoring.collector_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    exclude = [
        org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration::class,
        org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration::class
    ]
)
class CollectorServiceApplication

fun main(args: Array<String>) {
    runApplication<CollectorServiceApplication>(*args)
}
