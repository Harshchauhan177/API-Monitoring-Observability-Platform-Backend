package com.harsh.monitoring.collector_service.models.metadata

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "alerts")
data class Alert(
    @Id val id: String? = null,
    val serviceName: String,
    val endpoint: String,
    val message: String,
    val alertType: String,
    val timestamp: Long = System.currentTimeMillis()
)
