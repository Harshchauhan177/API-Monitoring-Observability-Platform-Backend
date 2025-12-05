package com.harsh.monitoring.collector_service.controllers

import com.harsh.monitoring.collector_service.models.logs.LogEntry
import com.harsh.monitoring.collector_service.models.logs.RateLimitHit
import com.harsh.monitoring.collector_service.models.metadata.Alert
import com.harsh.monitoring.collector_service.repositories.logs.LogEntryRepository
import com.harsh.monitoring.collector_service.repositories.logs.RateLimitHitRepository
import com.harsh.monitoring.collector_service.repositories.metadata.AlertRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class LogController(
    private val logRepo: LogEntryRepository,
    private val rateRepo: RateLimitHitRepository,
    private val alertRepo: AlertRepository
) {

    @PostMapping("/logs")
    fun saveLog(@RequestBody log: LogEntry): String {
        logRepo.save(log)

        // CREATE ALERTS
        if (log.latencyMs > 500) {
            alertRepo.save(Alert(
                serviceName = log.serviceName,
                endpoint = log.endpoint,
                message = "Slow API detected (>500ms)",
                alertType = "slow_api"
            ))
        }

        if (log.statusCode >= 500) {
            alertRepo.save(Alert(
                serviceName = log.serviceName,
                endpoint = log.endpoint,
                message = "Server error detected (5xx)",
                alertType = "error_5xx"
            ))
        }

        return "Log saved"
    }

    @PostMapping("/rate-limit-hit")
    fun saveRateLimitHit(@RequestBody rateHit: RateLimitHit): String {
        rateRepo.save(rateHit)

        alertRepo.save(Alert(
            serviceName = rateHit.serviceName,
            endpoint = "",
            message = "Rate limit exceeded",
            alertType = "rate_limit"
        ))

        return "Rate limit hit saved"
    }

    @GetMapping("/logs/all")
    fun getAllLogs(): List<LogEntry> =
        logRepo.findAll()
}
