package com.harsh.monitoring.collector_service.controllers

import com.harsh.monitoring.collector_service.models.metadata.ApiIssue
import com.harsh.monitoring.collector_service.repositories.metadata.ApiIssueRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/issues")
class IssueController(
    private val issueRepo: ApiIssueRepository
) {

    @GetMapping
    fun getAllIssues(): List<ApiIssue> = issueRepo.findAll()

    @GetMapping("/unresolved")
    fun getUnresolved(): List<ApiIssue> =
        issueRepo.findUnresolved().map { it.copy() }

    @PostMapping
    fun createIssue(@RequestBody issue: ApiIssue): ApiIssue =
        issueRepo.save(issue)

    @PutMapping("/{id}/resolve")
    fun resolveIssue(@PathVariable id: String): String {
        val issues = issueRepo.findUnresolved()
        val match = issues.find { it.id == id }
            ?: return "Issue not found"

        val resolved = match.copy(resolved = true)
        issueRepo.save(resolved)

        return "Issue resolved"
    }
}
