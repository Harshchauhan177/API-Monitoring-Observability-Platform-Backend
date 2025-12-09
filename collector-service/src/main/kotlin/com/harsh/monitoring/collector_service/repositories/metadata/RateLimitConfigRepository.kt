package com.harsh.monitoring.collector_service.repositories.metadata

import com.harsh.monitoring.collector_service.models.metadata.RateLimitConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class RateLimitConfigRepository(
    @Qualifier("metadataMongoTemplate")
    private val mongoTemplate: MongoTemplate
) {

    fun save(config: RateLimitConfig): RateLimitConfig {
        val updated = config.copy(updatedAt = System.currentTimeMillis())
        return mongoTemplate.save(updated, "rate_limit_configs")
    }

    fun findByServiceName(serviceName: String): RateLimitConfig? {
        val query = Query.query(Criteria.where("serviceName").`is`(serviceName))
        return mongoTemplate.findOne(query, RateLimitConfig::class.java, "rate_limit_configs")
    }

    fun findAll(): List<RateLimitConfig> =
        mongoTemplate.find(Query(), RateLimitConfig::class.java, "rate_limit_configs")

    fun deleteByServiceName(serviceName: String): Boolean {
        val query = Query.query(Criteria.where("serviceName").`is`(serviceName))
        val result = mongoTemplate.remove(query, RateLimitConfig::class.java, "rate_limit_configs")
        return result.deletedCount > 0
    }
}

