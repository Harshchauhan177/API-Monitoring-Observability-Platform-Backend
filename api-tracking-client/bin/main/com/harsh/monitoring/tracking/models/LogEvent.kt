package com.harsh.monitoring.tracking.models

data class LogEvent(
    val serviceName: String,
    val endpoint: String,
    val method: String,
    val requestSize: Long,
    val responseSize: Long,
    val statusCode: Int,
    val latencyMs: Long,
    val timestamp: Long
)
