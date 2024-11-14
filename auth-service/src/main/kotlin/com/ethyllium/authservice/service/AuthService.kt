package com.ethyllium.authservice.service

import com.ethyllium.authservice.exception.EmailExists
import com.ethyllium.authservice.exception.UsernameExists
import com.ethyllium.authservice.model.Token
import com.ethyllium.authservice.model.User
import com.ethyllium.authservice.util.RandomStringGenerator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val mongoTemplate: MongoTemplate,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val userService: UserService,
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java.simpleName)

    fun registerUser(user: User): User {
        user.hashedPassword = passwordEncoder.encode(user.hashedPassword)
        if (userService.getUserByUsername(user.userName) != null) {
            logger.error("Username ${user.userName} is already taken")
            throw UsernameExists(user.userName)
        }
        if (userService.getUserByEmail(user.email) != null) {
            logger.error("Email ${user.email} is already taken")
            throw EmailExists(user.email)
        }
        return mongoTemplate.save(user)
    }

    fun generateToken(username: String, userId: String) = mongoTemplate.save(
        Token(
            accessToken = jwtService.generateAccessToken(username),
            opaqueToken = RandomStringGenerator.generate(length = 32, allowSpecialChars = true),
            userId = userId,
            refreshToken = jwtService.generateAccessToken(username)
        )
    )

    fun isPasswordValid(rawPassword: String, encodedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }

    fun updateToken(token: Token): Token {
        val updatedToken = token.copy(
            accessTokenExpiration = System.currentTimeMillis() + JwtProperties.accessTokenExpiration,
            refreshTokenExpiration = System.currentTimeMillis() + JwtProperties.refreshTokenExpiration
        )
        mongoTemplate.updateFirst(
            Query(Criteria.where(Token::id.name).`is`(token.id)),
            Update.update(Token::accessTokenExpiration.name, updatedToken.accessTokenExpiration),
            Token::class.java
        )
        if (token.isAccessTokenExpired()) {
            val newToken = jwtService.generateAccessToken(userService.getUserById(token.userId)!!.username)
            mongoTemplate.updateFirst(
                Query(Criteria.where(Token::id.name).`is`(token.id)),
                Update.update(Token::accessToken.name, newToken),
                Token::class.java
            )
        }
        mongoTemplate.updateFirst(
            Query(Criteria.where(Token::id.name).`is`(token.id)),
            Update.update(Token::refreshTokenExpiration.name, updatedToken.refreshTokenExpiration),
            Token::class.java
        )
        return updatedToken
    }

}