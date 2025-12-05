package com.harsh.monitoring.collector_service.models.metadata

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("alerts")
data class Alert(
    @Id val id: String? = null,
    val serviceName: String,
    val endpoint: String,
    val message: String,
    val alertType: String,   // slow_api / error_5xx / rate_limit
    val timestamp: Instant = Instant.now()
)
