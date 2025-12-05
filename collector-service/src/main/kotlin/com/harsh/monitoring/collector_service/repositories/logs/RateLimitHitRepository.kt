package com.harsh.monitoring.collector_service.repositories.logs

import com.harsh.monitoring.collector_service.models.logs.RateLimitHit
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class RateLimitHitRepository(
    private val logsMongoTemplate: MongoTemplate
) {
    fun save(hit: RateLimitHit): RateLimitHit =
        logsMongoTemplate.save(hit)

    fun findAll(): List<RateLimitHit> =
        logsMongoTemplate.findAll(RateLimitHit::class.java)
}
