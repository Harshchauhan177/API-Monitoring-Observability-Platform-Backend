package com.harsh.monitoring.collector_service.repositories.logs

import com.harsh.monitoring.collector_service.models.logs.LogEntry
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class LogEntryRepository(
    @Qualifier("logsMongoTemplate")
    private val mongoTemplate: MongoTemplate
) {

    fun save(log: LogEntry): LogEntry =
        mongoTemplate.save(log, "log_entries")

    fun findAll(): List<LogEntry> =
        mongoTemplate.find(Query(), LogEntry::class.java, "log_entries")
}
