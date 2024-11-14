package com.ethyllium.authservice.service

import com.ethyllium.authservice.model.Token
import com.ethyllium.authservice.model.User
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

@Service
class UserService(
    private val mongoTemplate: MongoTemplate
) {


    fun getUserByEmail(email: String): User? {
        return Query.query(Criteria.where(User::email.name).`is`(email))
            .let { mongoTemplate.findOne(it, User::class.java) }
    }

    fun getUserByUsername(username: String): User? {
        return Query.query(Criteria.where(User::userName.name).`is`(username))
            .let { mongoTemplate.findOne(it, User::class.java) }
    }

    fun getUserById(userId: String): User? {
        return Query.query(Criteria.where(User::id.name).`is`(userId))
            .let { mongoTemplate.findOne(it, User::class.java) }
    }

    fun getUserByRefreshToken(phantomToken: String): User? {
        val token =
            mongoTemplate.findOne(Query(Criteria.where(Token::refreshToken.name).`is`(phantomToken)), Token::class.java)
                ?: return null
        return getUserById(token.userId)
    }

    fun getUserByOpaqueToken(phantomToken: String): User? {
        val token =
            mongoTemplate.findOne(Query(Criteria.where(Token::opaqueToken.name).`is`(phantomToken)), Token::class.java)
                ?: return null
        return getUserById(token.userId)
    }

    fun getTokenByRefreshToken(phantomToken: String): Token? {
        return mongoTemplate.findOne(
            Query(Criteria.where(Token::refreshToken.name).`is`(phantomToken)),
            Token::class.java
        )
    }
}