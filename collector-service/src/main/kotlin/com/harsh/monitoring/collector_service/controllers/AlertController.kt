package com.harsh.monitoring.collector_service.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.harsh.monitoring.collector_service.models.metadata.Alert
import com.harsh.monitoring.collector_service.models.metadata.AlertAuditTrail
import com.harsh.monitoring.collector_service.repositories.metadata.AlertRepository
import com.harsh.monitoring.collector_service.repositories.metadata.AlertAuditTrailRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/alerts")
class AlertController(
    private val alertRepo: AlertRepository,
    private val auditRepo: AlertAuditTrailRepository
) {

    private val mapper = ObjectMapper()

    @GetMapping
    fun getAllAlerts(): List<Alert> = alertRepo.findAll()

    @GetMapping("/{id}")
    fun getAlertById(@PathVariable id: String): Alert? =
        alertRepo.findById(id)

    @GetMapping("/{id}/audit")
    fun getAlertAuditTrail(@PathVariable id: String): List<AlertAuditTrail> =
        auditRepo.findByAlertId(id)

    @PostMapping
    fun createAlert(@RequestBody alert: Alert): Alert {
        val saved = alertRepo.save(alert)
        
        // Create audit trail for creation
        auditRepo.save(
            AlertAuditTrail(
                alertId = saved.id!!,
                action = "created",
                previousState = "none",
                newState = mapper.writeValueAsString(saved)
            )
        )
        
        return saved
    }

    @DeleteMapping("/{id}")
    fun deleteAlert(@PathVariable id: String): Map<String, String> {
        val alert = alertRepo.findById(id)
            ?: return mapOf("message" to "Alert not found")
        
        // Create audit trail before deletion
        auditRepo.save(
            AlertAuditTrail(
                alertId = id,
                action = "deleted",
                previousState = mapper.writeValueAsString(alert),
                newState = "deleted"
            )
        )
        
        alertRepo.deleteById(id)
        return mapOf("message" to "Alert deleted")
    }
}
