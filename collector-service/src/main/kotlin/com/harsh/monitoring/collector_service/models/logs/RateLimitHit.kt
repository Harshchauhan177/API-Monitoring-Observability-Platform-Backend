package com.harsh.monitoring.collector_service.models.logs

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "rate_limit_hits")
data class RateLimitHit(
    @Id val id: String? = null,
    val serviceName: String,
    val ipAddress: String,
    val timestamp: Long = System.currentTimeMillis()
)
