package com.harsh.monitoring.collector_service.repositories.logs

import com.harsh.monitoring.collector_service.models.logs.LogEntry
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class LogEntryRepository(
    private val logsMongoTemplate: MongoTemplate
) {
    fun save(log: LogEntry): LogEntry =
        logsMongoTemplate.save(log)

    fun findAll(): List<LogEntry> =
        logsMongoTemplate.findAll(LogEntry::class.java)
}
