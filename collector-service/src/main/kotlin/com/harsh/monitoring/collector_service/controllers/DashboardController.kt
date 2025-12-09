package com.harsh.monitoring.collector_service.controllers

import com.harsh.monitoring.collector_service.repositories.logs.LogEntryRepository
import com.harsh.monitoring.collector_service.repositories.logs.RateLimitHitRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class DashboardStats(
    val slowApiCount: Long,
    val brokenApiCount: Long,
    val rateLimitViolations: Long,
    val avgLatencyPerEndpoint: Map<String, Double>,
    val top5SlowEndpoints: List<EndpointStats>,
    val errorRate: Double
)

data class EndpointStats(
    val endpoint: String,
    val serviceName: String,
    val avgLatency: Double,
    val requestCount: Long
)

@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val logRepo: LogEntryRepository,
    private val rateRepo: RateLimitHitRepository
) {

    @GetMapping("/stats")
    fun getStats(): DashboardStats {
        val allLogs = logRepo.findAll()
        val allRateHits = rateRepo.findAll()

        // Slow API count (>500ms)
        val slowApiCount = allLogs.count { it.latencyMs > 500 }

        // Broken API count (5xx)
        val brokenApiCount = allLogs.count { it.statusCode >= 500 }

        // Rate limit violations
        val rateLimitViolations = allRateHits.size.toLong()

        // Average latency per endpoint
        val avgLatencyPerEndpoint = allLogs
            .groupBy { "${it.serviceName}:${it.endpoint}" }
            .mapValues { (_, logs) ->
                logs.map { it.latencyMs }.average()
            }

        // Top 5 slow endpoints
        val top5SlowEndpoints = allLogs
            .groupBy { "${it.serviceName}:${it.endpoint}" }
            .map { (key, logs) ->
                val parts = key.split(":")
                EndpointStats(
                    endpoint = parts.getOrElse(1) { "" },
                    serviceName = parts.getOrElse(0) { "" },
                    avgLatency = logs.map { it.latencyMs }.average(),
                    requestCount = logs.size.toLong()
                )
            }
            .sortedByDescending { it.avgLatency }
            .take(5)

        // Error rate (5xx / total)
        val totalRequests = allLogs.size.toLong()
        val errorRate = if (totalRequests > 0) {
            (brokenApiCount.toDouble() / totalRequests) * 100
        } else {
            0.0
        }

        return DashboardStats(
            slowApiCount = slowApiCount.toLong(),
            brokenApiCount = brokenApiCount.toLong(),
            rateLimitViolations = rateLimitViolations,
            avgLatencyPerEndpoint = avgLatencyPerEndpoint,
            top5SlowEndpoints = top5SlowEndpoints,
            errorRate = errorRate
        )
    }
}

