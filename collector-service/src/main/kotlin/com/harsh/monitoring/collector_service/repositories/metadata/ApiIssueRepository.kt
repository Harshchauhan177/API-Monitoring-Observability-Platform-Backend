package com.harsh.monitoring.collector_service.repositories.metadata

import com.harsh.monitoring.collector_service.models.metadata.ApiIssue
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class ApiIssueRepository(
    @Qualifier("metadataMongoTemplate")
    private val mongoTemplate: MongoTemplate
) {

    fun save(issue: ApiIssue): ApiIssue =
        mongoTemplate.save(issue, "issues")

    fun findAll(): List<ApiIssue> =
        mongoTemplate.find(Query(), ApiIssue::class.java, "issues")

    fun findUnresolved(): List<ApiIssue> {
        val query = Query.query(Criteria.where("resolved").`is`(false))
        return mongoTemplate.find(query, ApiIssue::class.java, "issues")
    }
}
