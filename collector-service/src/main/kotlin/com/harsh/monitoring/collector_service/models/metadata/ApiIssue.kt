package com.harsh.monitoring.collector_service.models.metadata

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("issues")
data class ApiIssue(
    @Id val id: String? = null,
    val serviceName: String,
    val endpoint: String,
    val issueType: String, // slow_api or broken_api
    val isResolved: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val resolvedAt: Instant? = null
)
