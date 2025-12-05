package com.harsh.monitoring.collector_service.repositories.metadata

import com.harsh.monitoring.collector_service.models.metadata.UserAccount
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class UserAccountRepository(
    private val metadataMongoTemplate: MongoTemplate
) {
    fun save(user: UserAccount): UserAccount =
        metadataMongoTemplate.save(user)

    fun findByUsername(username: String): UserAccount? =
        metadataMongoTemplate.find(
            org.springframework.data.mongodb.core.query.Query(
                org.springframework.data.mongodb.core.query.Criteria.where("username").`is`(username)
            ),
            UserAccount::class.java
        ).firstOrNull()
}
