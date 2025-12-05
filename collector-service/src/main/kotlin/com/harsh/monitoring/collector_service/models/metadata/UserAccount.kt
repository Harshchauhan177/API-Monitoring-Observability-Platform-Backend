package com.harsh.monitoring.collector_service.models.metadata

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class UserAccount(
    @Id val id: String? = null,
    val username: String,
    val password: String,
    val role: String = "USER"
)
