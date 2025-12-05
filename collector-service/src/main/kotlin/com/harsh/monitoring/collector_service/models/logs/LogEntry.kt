package com.harsh.monitoring.collector_service.models.logs

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("api_logs")
data class LogEntry(
    @Id val id: String? = null,
    val serviceName: String,
    val endpoint: String,
    val method: String,
    val requestSize: Long,
    val responseSize: Long,
    val statusCode: Int,
    val latencyMs: Long,
    val timestamp: Instant = Instant.now()
)
