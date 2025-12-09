package com.harsh.monitoring.collector_service.repositories.metadata

import com.harsh.monitoring.collector_service.models.metadata.AlertAuditTrail
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class AlertAuditTrailRepository(
    @Qualifier("metadataMongoTemplate")
    private val mongoTemplate: MongoTemplate
) {

    fun save(audit: AlertAuditTrail): AlertAuditTrail =
        mongoTemplate.save(audit, "alert_audit_trail")

    fun findByAlertId(alertId: String): List<AlertAuditTrail> {
        val query = Query.query(Criteria.where("alertId").`is`(alertId))
        return mongoTemplate.find(query, AlertAuditTrail::class.java, "alert_audit_trail")
            .sortedByDescending { it.timestamp }
    }

    fun findAll(): List<AlertAuditTrail> =
        mongoTemplate.find(Query(), AlertAuditTrail::class.java, "alert_audit_trail")
}

