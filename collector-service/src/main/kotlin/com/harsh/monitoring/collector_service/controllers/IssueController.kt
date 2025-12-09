package com.harsh.monitoring.collector_service.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.harsh.monitoring.collector_service.models.metadata.ApiIssue
import com.harsh.monitoring.collector_service.models.metadata.IssueAuditTrail
import com.harsh.monitoring.collector_service.repositories.metadata.ApiIssueRepository
import com.harsh.monitoring.collector_service.repositories.metadata.IssueAuditTrailRepository
import org.springframework.web.bind.annotation.*

data class ResolveIssueRequest(
    val version: Long? = null  // Optional version for optimistic locking
)

data class ResolveIssueResponse(
    val success: Boolean,
    val message: String,
    val conflict: Boolean = false  // True if version conflict occurred
)

@RestController
@RequestMapping("/api/issues")
class IssueController(
    private val issueRepo: ApiIssueRepository,
    private val auditRepo: IssueAuditTrailRepository
) {

    private val mapper = ObjectMapper()

    @GetMapping
    fun getAllIssues(): List<ApiIssue> = issueRepo.findAll()

    @GetMapping("/unresolved")
    fun getUnresolved(): List<ApiIssue> =
        issueRepo.findUnresolved().map { it.copy() }

    @GetMapping("/{id}")
    fun getIssueById(@PathVariable id: String): ApiIssue? =
        issueRepo.findById(id)

    @GetMapping("/{id}/audit")
    fun getIssueAuditTrail(@PathVariable id: String): List<IssueAuditTrail> =
        auditRepo.findByIssueId(id)

    @PostMapping
    fun createIssue(@RequestBody issue: ApiIssue): ApiIssue {
        val saved = issueRepo.save(issue)
        
        // Create audit trail for creation
        auditRepo.save(
            IssueAuditTrail(
                issueId = saved.id!!,
                action = "created",
                previousState = "none",
                newState = mapper.writeValueAsString(saved)
            )
        )
        
        return saved
    }

    @PutMapping("/{id}/resolve")
    fun resolveIssue(
        @PathVariable id: String,
        @RequestBody(required = false) request: ResolveIssueRequest?
    ): ResolveIssueResponse {
        val issue = issueRepo.findById(id)
            ?: return ResolveIssueResponse(
                success = false,
                message = "Issue not found"
            )

        if (issue.resolved) {
            return ResolveIssueResponse(
                success = false,
                message = "Issue already resolved"
            )
        }

        // Try atomic update with optimistic locking
        val expectedVersion = request?.version ?: issue.version
        val success = issueRepo.resolveIssueAtomically(id, expectedVersion)

        if (!success) {
            // Version conflict or already resolved
            return ResolveIssueResponse(
                success = false,
                message = "Issue was modified by another user. Please refresh and try again.",
                conflict = true
            )
        }

        // Get updated issue
        val updatedIssue = issueRepo.findById(id)!!

        // Create audit trail
        auditRepo.save(
            IssueAuditTrail(
                issueId = id,
                action = "resolved",
                previousState = mapper.writeValueAsString(issue),
                newState = mapper.writeValueAsString(updatedIssue)
            )
        )

        return ResolveIssueResponse(
            success = true,
            message = "Issue resolved successfully"
        )
    }
}
