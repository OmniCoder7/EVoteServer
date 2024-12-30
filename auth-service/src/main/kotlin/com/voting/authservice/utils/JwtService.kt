package com.voting.authservice.utils

import com.voting.authservice.service.RedisService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Component
class JwtService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration.access}") val accessTokenExpiration: Long,
    @Value("\${jwt.expiration.refresh}") val refreshTokenExpiration: Long,
    private val redisService: RedisService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val secretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    @OptIn(ExperimentalUuidApi::class)
    private fun generate(
        username: String,
        additionalClaims: Map<String, Any> = emptyMap(),
        expirationDate: Date = Date(System.currentTimeMillis() + accessTokenExpiration)
    ): String {
        val jti = Uuid.random().toHexString()
        return Jwts.builder().claims(additionalClaims).subject(username)
            .issuedAt(Date(System.currentTimeMillis())).expiration(expirationDate).id(jti)
            .signWith(secretKey, Jwts.SIG.HS256).compact()
    }

    fun generateAccessToken(username: String,
                            additionalClaims: Map<String, Any> = emptyMap()
    ) = generate(username, additionalClaims, Date(System.currentTimeMillis() + accessTokenExpiration))

    fun generateRefreshToken(username: String,
                             additionalClaims: Map<String, Any> = emptyMap()
    ) = generate(username, additionalClaims, Date(System.currentTimeMillis() + refreshTokenExpiration))


    fun isValid(token: String, userDetails: UserDetails): Boolean {
        try {
            validateTokenClaims(token, userDetails)
            return !isRevoked(token)
        } catch (e: Exception) {
            logger.info("Token validation failed: ${e.message}")
            return false
        }
    }

    private fun validateTokenClaims(token: String, userDetails: UserDetails) {
        val claims = getAllClaims(token)

        val email = claims.subject ?: throw IllegalArgumentException("Token subject (email) is missing")
        if (email != userDetails.username) {
            throw IllegalArgumentException("Token subject (email) does not match the user")
        }

        val expiration = claims.expiration ?: throw IllegalArgumentException("Token expiration is missing")
        if (expiration.before(Date())) {
            throw IllegalArgumentException("Token has expired")
        }

        val issuedAt = claims.issuedAt ?: throw IllegalArgumentException("Token issued at is missing")
        if (issuedAt.after(Date())) {
            throw IllegalArgumentException("Token issued at is in the future")
        }

        validateCustomClaims(claims)
    }

    private fun validateCustomClaims(claims: Claims) {
        val roles = claims["roles"] ?: throw IllegalArgumentException("Roles claim is missing")
        if (roles !is List<*> || roles.isEmpty()) {
            throw IllegalArgumentException("Roles claim is invalid")
        }

        if (!roles.contains("USER")) {
            throw IllegalArgumentException("User does not have the required role")
        }
    }

    fun getClaim(token: String, claimName: String): Any? {
        return getAllClaims(token)[claimName]
    }

    fun extractEmail(token: String): String? = getAllClaims(token).subject

    private fun getAllClaims(token: String): Claims {
        val parser = Jwts.parser().verifyWith(secretKey).build()
        return parser.parseSignedClaims(token).payload
    }

    fun isRevoked(token: String): Boolean {
        val jti = getClaim(token, "jti") as String? ?: throw IllegalArgumentException("Token ID (jti) is missing")
        return redisService.isTokenPresent(jti) // Check Redis for the revoked JTI
    }

    fun revokeToken(token: String) {
        val jti = getClaim(token, "jti") as String? ?: throw IllegalArgumentException("Token ID (jti) is missing")
        redisService.save(value = jti, ttl = Duration.of(getAllClaims(token).expiration.time, ChronoUnit.MINUTES), key = ACCESS_TOKEN)
    }
}