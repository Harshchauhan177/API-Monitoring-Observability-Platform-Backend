package com.harsh.monitoring.collector_service.models.logs

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "log_entries")
data class LogEntry(
    @Id val id: String? = null,
    val serviceName: String,
    val endpoint: String,
    val method: String,
    val statusCode: Int,
    val latencyMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)
