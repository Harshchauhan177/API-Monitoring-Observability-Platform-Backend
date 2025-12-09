package com.harsh.monitoring.tracking

import com.fasterxml.jackson.databind.ObjectMapper
import com.harsh.monitoring.tracking.models.LogEvent
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

data class RateLimitHitEvent(
    val serviceName: String,
    val ipAddress: String,
    val timestamp: Long = System.currentTimeMillis()
)

class LogSender(
    private val collectorUrl: String
) {

    private val rest = RestTemplate()
    private val mapper = ObjectMapper()

    fun sendLog(log: LogEvent) {
        try {
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON

            val json = mapper.writeValueAsString(log)
            val entity = HttpEntity(json, headers)

            // Send to collector-service
            rest.postForEntity("$collectorUrl/api/logs", entity, String::class.java)

        } catch (e: Exception) {
            println("Failed to send log: ${e.message}")
        }
    }

    fun sendRateLimitHit(hit: RateLimitHitEvent) {
        try {
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON

            val json = mapper.writeValueAsString(hit)
            val entity = HttpEntity(json, headers)

            // Send rate limit hit to collector-service
            rest.postForEntity("$collectorUrl/api/logs/rate-limit", entity, String::class.java)

        } catch (e: Exception) {
            println("Failed to send rate limit hit: ${e.message}")
        }
    }
}
