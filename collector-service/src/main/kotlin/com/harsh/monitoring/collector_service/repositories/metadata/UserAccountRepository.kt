package com.harsh.monitoring.collector_service.repositories.metadata

import com.harsh.monitoring.collector_service.models.metadata.UserAccount
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class UserAccountRepository(
    @Qualifier("metadataMongoTemplate")
    private val mongoTemplate: MongoTemplate
) {

    fun save(user: UserAccount): UserAccount =
        mongoTemplate.save(user, "users")

    fun findByUsername(username: String): UserAccount? {
        val query = Query.query(Criteria.where("username").`is`(username))
        return mongoTemplate.findOne(query, UserAccount::class.java, "users")
    }

    fun findAll(): List<UserAccount> =
        mongoTemplate.find(Query(), UserAccount::class.java, "users")
}
