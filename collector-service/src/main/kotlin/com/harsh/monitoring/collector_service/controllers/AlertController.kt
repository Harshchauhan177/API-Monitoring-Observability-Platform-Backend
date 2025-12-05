package com.harsh.monitoring.collector_service.controllers

import com.harsh.monitoring.collector_service.models.metadata.Alert
import com.harsh.monitoring.collector_service.repositories.metadata.AlertRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/alerts")
class AlertController(
    private val alertRepo: AlertRepository
) {

    @GetMapping
    fun getAllAlerts(): List<Alert> = alertRepo.findAll()

    @PostMapping
    fun createAlert(@RequestBody alert: Alert): Alert =
        alertRepo.save(alert)

    @DeleteMapping("/{id}")
    fun deleteAlert(@PathVariable id: String): String {
        alertRepo.deleteById(id)
        return "Alert deleted"
    }
}
