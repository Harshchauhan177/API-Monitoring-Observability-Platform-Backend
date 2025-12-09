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

        // Get request size
        val requestSize = request.contentLengthLong.takeIf { it >= 0 } ?: 0

        // Get response size from Content-Length header if available
        // Note: For accurate response size, a response wrapper filter would be needed
        // This is a best-effort approach using headers
        val responseSizeHeader = response.getHeader("Content-Length")
        val responseSize = responseSizeHeader?.toLongOrNull() ?: 0

        val log = LogEvent(
            serviceName = serviceName,
            endpoint = request.requestURI,
            method = request.method,
            requestSize = requestSize,
            responseSize = responseSize,
            statusCode = response.status,
            latencyMs = latency,
            timestamp = System.currentTimeMillis()
        )

        // Check rate limiter and send event if exceeded
        if (rateLimiter.hit()) {
            println("Rate limit exceeded for $serviceName")
            // Send rate limit hit event to collector
            val rateLimitHit = RateLimitHitEvent(
                serviceName = serviceName,
                ipAddress = request.remoteAddr ?: "unknown"
            )
            logSender.sendRateLimitHit(rateLimitHit)
        }

        // Send log to collector-service (always send, even if rate limited)
        logSender.sendLog(log)
    }
}
