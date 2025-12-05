package com.harsh.monitoring.collector_service.models.logs

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("rate_limit_hits")
data class RateLimitHit(
    @Id val id: String? = null,
    val serviceName: String,
    val limit: Int,
    val timestamp: Instant = Instant.now()
)
