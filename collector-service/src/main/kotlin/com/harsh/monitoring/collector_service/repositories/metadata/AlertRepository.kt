package com.harsh.monitoring.collector_service.repositories.metadata

import com.harsh.monitoring.collector_service.models.metadata.Alert
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class AlertRepository(
    private val metadataMongoTemplate: MongoTemplate
) {
    fun save(alert: Alert): Alert =
        metadataMongoTemplate.save(alert)

    fun findAll(): List<Alert> =
        metadataMongoTemplate.findAll(Alert::class.java)
}
