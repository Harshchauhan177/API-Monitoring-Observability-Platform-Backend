package com.harsh.monitoring.tracking

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class TrackingConfig : WebMvcConfigurer {

    @Bean
    fun trackingInterceptor(): TrackingInterceptor {
        return TrackingInterceptor(
            serviceName = "sample-service",                // CHANGE PER SERVICE
            logSender = LogSender("http://localhost:8080"), // Collector service URL
            rateLimiter = RateLimiter(100)                  // 100 req/sec
        )
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(trackingInterceptor())
    }
}
