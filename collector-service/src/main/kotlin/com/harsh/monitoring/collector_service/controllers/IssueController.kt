package com.harsh.monitoring.collector_service.controllers

import com.harsh.monitoring.collector_service.models.metadata.ApiIssue
import com.harsh.monitoring.collector_service.repositories.metadata.ApiIssueRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/issues")
class IssueController(
    private val issueRepo: ApiIssueRepository
) {

    @PostMapping("/create")
    fun createIssue(@RequestBody issue: ApiIssue): String {
        issueRepo.save(issue)
        return "Issue created"
    }

    @PostMapping("/{id}/resolve")
    fun resolveIssue(@PathVariable id: String): String {
        val allIssues = issueRepo.findUnresolved()
        val issue = allIssues.find { it.id == id } ?: return "Issue not found"

        val resolved = issue.copy(isResolved = true, resolvedAt = java.time.Instant.now())
        issueRepo.save(resolved)

        return "Issue resolved"
    }

    @GetMapping("/unresolved")
    fun getUnresolved(): List<ApiIssue> =
        issueRepo.findUnresolved()
}
