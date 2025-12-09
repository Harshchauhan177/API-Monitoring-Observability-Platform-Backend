package com.harsh.monitoring.collector_service.repositories.metadata

import com.harsh.monitoring.collector_service.models.metadata.ApiIssue
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class ApiIssueRepository(
    @Qualifier("metadataMongoTemplate")
    private val mongoTemplate: MongoTemplate
) {

    fun save(issue: ApiIssue): ApiIssue =
        mongoTemplate.save(issue, "issues")

    fun findById(id: String): ApiIssue? {
        val query = Query.query(Criteria.where("_id").`is`(id))
        return mongoTemplate.findOne(query, ApiIssue::class.java, "issues")
    }

    fun findAll(): List<ApiIssue> =
        mongoTemplate.find(Query(), ApiIssue::class.java, "issues")

    fun findUnresolved(): List<ApiIssue> {
        val query = Query.query(Criteria.where("resolved").`is`(false))
        return mongoTemplate.find(query, ApiIssue::class.java, "issues")
    }

    // Atomic update with version check for optimistic locking
    fun resolveIssueAtomically(id: String, expectedVersion: Long?): Boolean {
        val query = Query.query(
            Criteria.where("_id").`is`(id)
                .and("resolved").`is`(false)
                .apply {
                    expectedVersion?.let {
                        and("version").`is`(it)
                    }
                }
        )
        
        val update = Update()
            .set("resolved", true)
            .inc("version", 1)
        
        val result = mongoTemplate.updateFirst(query, update, ApiIssue::class.java, "issues")
        return result.modifiedCount > 0
    }
}
