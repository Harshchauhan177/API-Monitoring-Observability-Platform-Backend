package com.harsh.monitoring.tracking

class RateLimiter(private val limitPerSecond: Int) {

    private var lastTimestamp = System.currentTimeMillis()
    private var requestCount = 0

    fun hit(): Boolean {
        val now = System.currentTimeMillis()

        // Reset every second
        if (now - lastTimestamp >= 1000) {
            lastTimestamp = now
            requestCount = 0
        }

        requestCount++

        // TRUE means limit exceeded
        return requestCount > limitPerSecond
    }
}
