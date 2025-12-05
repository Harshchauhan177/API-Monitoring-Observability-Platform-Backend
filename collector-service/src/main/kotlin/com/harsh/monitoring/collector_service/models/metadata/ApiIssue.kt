package com.harsh.monitoring.collector_service.models.metadata

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "api_issues")
data class ApiIssue(
    @Id val id: String? = null,
    val serviceName: String,
    val endpoint: String,
    val issueType: String,
    val resolved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
