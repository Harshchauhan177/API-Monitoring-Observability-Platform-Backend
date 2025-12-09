package com.harsh.monitoring.collector_service.repositories.metadata

import com.harsh.monitoring.collector_service.models.metadata.Alert
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class AlertRepository(
    @Qualifier("metadataMongoTemplate")
    private val mongoTemplate: MongoTemplate
) {

    fun save(alert: Alert): Alert =
        mongoTemplate.save(alert, "alerts")

    fun findAll(): List<Alert> =
        mongoTemplate.find(Query(), Alert::class.java, "alerts")

    fun findById(id: String): Alert? {
        val query = Query.query(org.springframework.data.mongodb.core.query.Criteria.where("_id").`is`(id))
        return mongoTemplate.findOne(query, Alert::class.java, "alerts")
    }

    fun deleteById(id: String) {
        val query = Query.query(org.springframework.data.mongodb.core.query.Criteria.where("_id").`is`(id))
        mongoTemplate.remove(query, Alert::class.java, "alerts")
    }
}
