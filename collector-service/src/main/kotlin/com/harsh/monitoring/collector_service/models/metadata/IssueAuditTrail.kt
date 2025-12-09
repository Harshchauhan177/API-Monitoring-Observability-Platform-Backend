package com.harsh.monitoring.collector_service.models.metadata

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "issue_audit_trail")
data class IssueAuditTrail(
    @Id val id: String? = null,
    val issueId: String,
    val action: String,  // "resolved", "created", "updated"
    val performedBy: String? = null,  // Could be username if auth is added
    val previousState: String,  // JSON or description
    val newState: String,
    val timestamp: Long = System.currentTimeMillis()
)

