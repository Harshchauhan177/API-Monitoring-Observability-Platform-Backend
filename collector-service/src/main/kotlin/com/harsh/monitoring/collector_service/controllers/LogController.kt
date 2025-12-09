package com.harsh.monitoring.collector_service.controllers

import com.harsh.monitoring.collector_service.models.logs.LogEntry
import com.harsh.monitoring.collector_service.models.logs.RateLimitHit
import com.harsh.monitoring.collector_service.models.metadata.Alert
import com.harsh.monitoring.collector_service.models.metadata.ApiIssue
import com.harsh.monitoring.collector_service.repositories.logs.LogEntryRepository
import com.harsh.monitoring.collector_service.repositories.logs.RateLimitHitRepository
import com.harsh.monitoring.collector_service.repositories.metadata.AlertRepository
import com.harsh.monitoring.collector_service.repositories.metadata.ApiIssueRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/logs")
class LogController(
    private val logRepo: LogEntryRepository,
    private val rateRepo: RateLimitHitRepository,
    private val alertRepo: AlertRepository,
    private val issueRepo: ApiIssueRepository
) {

    @PostMapping
    fun saveLog(@RequestBody log: LogEntry): String {
        logRepo.save(log)

        if (log.latencyMs > 500) {
            alertRepo.save(
                Alert(
                    serviceName = log.serviceName,
                    endpoint = log.endpoint,
                    message = "Slow API detected (>500ms)",
                    alertType = "slow_api"
                )
            )
        }

        if (log.statusCode >= 500) {

            alertRepo.save(
                Alert(
                    serviceName = log.serviceName,
                    endpoint = log.endpoint,
                    message = "Server error detected (5xx)",
                    alertType = "error_5xx"
                )
            )

            issueRepo.save(
                ApiIssue(
                    serviceName = log.serviceName,
                    endpoint = log.endpoint,
                    errorMessage = "Internal server error (5xx)",
                    issueType = "server_error"
                )
            )
        }

        return "Log saved"
    }

    @PostMapping("/rate-limit")
    fun saveRateLimitHit(@RequestBody hit: RateLimitHit): String {
        rateRepo.save(hit)

        alertRepo.save(
            Alert(
                serviceName = hit.serviceName,
                endpoint = "",
                message = "Rate limit exceeded",
                alertType = "rate_limit"
            )
        )

        issueRepo.save(
            ApiIssue(
                serviceName = hit.serviceName,
                endpoint = "",
                errorMessage = "Rate limit exceeded",
                issueType = "rate_limit"
            )
        )

        return "Rate limit hit saved"
    }

    @GetMapping("/all")
    fun getAllLogs(): List<LogEntry> = logRepo.findAll()

    @GetMapping("/filtered")
    fun getFilteredLogs(
        @RequestParam(required = false) serviceName: String?,
        @RequestParam(required = false) endpoint: String?,
        @RequestParam(required = false) startDate: Long?,
        @RequestParam(required = false) endDate: Long?,
        @RequestParam(required = false) statusCode: Int?,
        @RequestParam(required = false, defaultValue = "false") slowOnly: Boolean,
        @RequestParam(required = false, defaultValue = "false") brokenOnly: Boolean,
        @RequestParam(required = false, defaultValue = "false") rateLimitOnly: Boolean
    ): List<LogEntry> {
        return logRepo.findFiltered(
            serviceName = serviceName,
            endpoint = endpoint,
            startDate = startDate,
            endDate = endDate,
            statusCode = statusCode,
            slowOnly = slowOnly,
            brokenOnly = brokenOnly,
            rateLimitOnly = rateLimitOnly
        )
    }
}
