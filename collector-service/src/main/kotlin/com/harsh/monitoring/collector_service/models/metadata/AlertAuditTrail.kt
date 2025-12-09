package com.harsh.monitoring.collector_service.models.metadata

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "alert_audit_trail")
data class AlertAuditTrail(
    @Id val id: String? = null,
    val alertId: String,
    val action: String,  // "created", "acknowledged", "resolved", "deleted"
    val performedBy: String? = null,
    val previousState: String,
    val newState: String,
    val timestamp: Long = System.currentTimeMillis()
)

