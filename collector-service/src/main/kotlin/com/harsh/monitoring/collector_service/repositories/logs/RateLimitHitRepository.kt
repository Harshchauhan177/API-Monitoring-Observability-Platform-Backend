package com.harsh.monitoring.collector_service.repositories.logs

import com.harsh.monitoring.collector_service.models.logs.RateLimitHit
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class RateLimitHitRepository(
    @Qualifier("logsMongoTemplate")
    private val mongoTemplate: MongoTemplate
) {

    fun save(hit: RateLimitHit): RateLimitHit =
        mongoTemplate.save(hit, "rate_limit_hits")

    fun findAll(): List<RateLimitHit> =
        mongoTemplate.find(Query(), RateLimitHit::class.java, "rate_limit_hits")
}
