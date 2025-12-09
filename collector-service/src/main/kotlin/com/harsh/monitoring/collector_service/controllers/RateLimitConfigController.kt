package com.harsh.monitoring.collector_service.controllers

import com.harsh.monitoring.collector_service.models.metadata.RateLimitConfig
import com.harsh.monitoring.collector_service.repositories.metadata.RateLimitConfigRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rate-limit-config")
class RateLimitConfigController(
    private val configRepo: RateLimitConfigRepository
) {

    @GetMapping
    fun getAllConfigs(): List<RateLimitConfig> = configRepo.findAll()

    @GetMapping("/service/{serviceName}")
    fun getConfigByService(@PathVariable serviceName: String): RateLimitConfig? =
        configRepo.findByServiceName(serviceName)

    @PostMapping
    fun createOrUpdateConfig(@RequestBody config: RateLimitConfig): RateLimitConfig {
        // If config exists, update it; otherwise create new
        val existing = configRepo.findByServiceName(config.serviceName)
        return if (existing != null) {
            configRepo.save(
                config.copy(
                    id = existing.id,
                    createdAt = existing.createdAt
                )
            )
        } else {
            configRepo.save(config)
        }
    }

    @PutMapping("/{id}")
    fun updateConfig(
        @PathVariable id: String,
        @RequestBody config: RateLimitConfig
    ): RateLimitConfig {
        return configRepo.save(config.copy(id = id))
    }

    @DeleteMapping("/service/{serviceName}")
    fun deleteConfigByService(@PathVariable serviceName: String): Map<String, String> {
        val deleted = configRepo.deleteByServiceName(serviceName)
        return if (deleted) {
            mapOf("message" to "Rate limit config deleted for service: $serviceName")
        } else {
            mapOf("message" to "Config not found for service: $serviceName")
        }
    }
}

