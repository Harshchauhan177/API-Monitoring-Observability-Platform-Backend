package com.harsh.monitoring.tracking

import com.harsh.monitoring.tracking.models.LogEvent
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor

class TrackingInterceptor(
    private val serviceName: String,
    private val logSender: LogSender,
    private val rateLimiter: RateLimiter
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        // Store start time
        request.setAttribute("startTime", System.currentTimeMillis())
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val startTime = request.getAttribute("startTime") as Long
        val latency = System.currentTimeMillis() - startTime

        val log = LogEvent(
            serviceName = serviceName,
            endpoint = request.requestURI,
            method = request.method,
            requestSize = request.contentLengthLong.takeIf { it >= 0 } ?: 0,
            responseSize = response.contentType?.length?.toLong() ?: 0,
            statusCode = response.status,
            latencyMs = latency,
            timestamp = System.currentTimeMillis()
        )

        // Rate limiter hit
        if (rateLimiter.hit()) {
            println("Rate limit exceeded for $serviceName")
        }

        // Send log to collector-service
        logSender.sendLog(log)
    }
}
