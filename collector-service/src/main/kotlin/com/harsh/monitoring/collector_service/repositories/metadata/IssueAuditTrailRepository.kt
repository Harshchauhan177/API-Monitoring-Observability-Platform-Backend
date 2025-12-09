package com.harsh.monitoring.collector_service.repositories.metadata

import com.harsh.monitoring.collector_service.models.metadata.IssueAuditTrail
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class IssueAuditTrailRepository(
    @Qualifier("metadataMongoTemplate")
    private val mongoTemplate: MongoTemplate
) {

    fun save(audit: IssueAuditTrail): IssueAuditTrail =
        mongoTemplate.save(audit, "issue_audit_trail")

    fun findByIssueId(issueId: String): List<IssueAuditTrail> {
        val query = Query.query(Criteria.where("issueId").`is`(issueId))
        return mongoTemplate.find(query, IssueAuditTrail::class.java, "issue_audit_trail")
            .sortedByDescending { it.timestamp }
    }

    fun findAll(): List<IssueAuditTrail> =
        mongoTemplate.find(Query(), IssueAuditTrail::class.java, "issue_audit_trail")
}

