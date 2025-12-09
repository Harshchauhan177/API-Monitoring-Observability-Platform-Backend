package com.harsh.monitoring.tracking

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "monitoring")
data class MonitoringProperties(
    var rateLimit: RateLimitConfig = RateLimitConfig()
) {
    data class RateLimitConfig(
        var service: String = "default-service",
        var limit: Int = 100
    )
}

