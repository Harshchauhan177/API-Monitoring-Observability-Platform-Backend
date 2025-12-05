package com.harsh.monitoring.collector_service.controllers

import com.harsh.monitoring.collector_service.models.metadata.Alert
import com.harsh.monitoring.collector_service.repositories.metadata.AlertRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/alerts")
class AlertController(
    private val repo: AlertRepository
) {
    @GetMapping("/all")
    fun getAllAlerts(): List<Alert> =
        repo.findAll()
}
