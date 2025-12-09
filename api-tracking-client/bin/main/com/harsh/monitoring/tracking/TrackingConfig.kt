package com.harsh.monitoring.tracking

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableConfigurationProperties(MonitoringProperties::class)
class TrackingConfig(
    private val monitoringProperties: MonitoringProperties
) : WebMvcConfigurer {

    @Value("\${monitoring.collector.url:http://localhost:8080}")
    private lateinit var collectorUrl: String

    @Bean
    fun trackingInterceptor(): TrackingInterceptor {
        val serviceName = monitoringProperties.rateLimit.service
        val rateLimit = monitoringProperties.rateLimit.limit

        return TrackingInterceptor(
            serviceName = serviceName,
            logSender = LogSender(collectorUrl),
            rateLimiter = RateLimiter(rateLimit)
        )
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(trackingInterceptor())
    }
}
