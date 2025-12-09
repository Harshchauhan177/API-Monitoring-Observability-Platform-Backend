package com.harsh.monitoring.collector_service.repositories.logs

import com.harsh.monitoring.collector_service.models.logs.LogEntry
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.ZoneId

@Repository
class LogEntryRepository(
    @Qualifier("logsMongoTemplate")
    private val mongoTemplate: MongoTemplate
) {

    fun save(log: LogEntry): LogEntry =
        mongoTemplate.save(log, "log_entries")

    fun findAll(): List<LogEntry> =
        mongoTemplate.find(Query(), LogEntry::class.java, "log_entries")

    fun findFiltered(
        serviceName: String? = null,
        endpoint: String? = null,
        startDate: Long? = null,
        endDate: Long? = null,
        statusCode: Int? = null,
        slowOnly: Boolean = false,
        brokenOnly: Boolean = false,
        rateLimitOnly: Boolean = false
    ): List<LogEntry> {
        val query = Query()
        val criteria = Criteria()

        serviceName?.let { criteria.and("serviceName").`is`(it) }
        endpoint?.let { criteria.and("endpoint").regex(it, "i") }
        startDate?.let { criteria.and("timestamp").gte(it) }
        endDate?.let { criteria.and("timestamp").lte(it) }
        statusCode?.let { criteria.and("statusCode").`is`(it) }

        if (slowOnly) {
            criteria.and("latencyMs").gt(500)
        }

        if (brokenOnly) {
            criteria.and("statusCode").gte(500)
        }

        query.addCriteria(criteria)
        return mongoTemplate.find(query, LogEntry::class.java, "log_entries")
    }
}
