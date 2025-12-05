package com.harsh.monitoring.collector_service.repositories.metadata

import com.harsh.monitoring.collector_service.models.metadata.ApiIssue
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Criteria

@Repository
class ApiIssueRepository(
    private val metadataMongoTemplate: MongoTemplate
) {
    fun save(issue: ApiIssue): ApiIssue =
        metadataMongoTemplate.save(issue)

    fun findUnresolved(): List<ApiIssue> =
        metadataMongoTemplate.find(
            Query(Criteria.where("isResolved").`is`(false)),
            ApiIssue::class.java
        )
}
