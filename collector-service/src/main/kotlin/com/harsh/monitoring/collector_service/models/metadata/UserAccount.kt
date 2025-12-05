package com.harsh.monitoring.collector_service.models.metadata

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
data class UserAccount(
    @Id val id: String? = null,
    val username: String,
    val passwordHash: String,
    val role: String = "USER"
)
