package com.voting.authservice.ott

import com.voting.authservice.exception.UserNotFound
import com.voting.authservice.model.OTT
import com.voting.authservice.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest
import org.springframework.security.authentication.ott.OneTimeToken
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken
import org.springframework.security.authentication.ott.OneTimeTokenService
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Service
class OTTService(
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${ott.ttl}") private val ottTtl: Long,
    private val userService: UserService
) : OneTimeTokenService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @OptIn(ExperimentalUuidApi::class)
    override fun generate(request: GenerateOneTimeTokenRequest): OneTimeToken {
        logger.info("Generating new One Time Token for user : ${request.username}")
        val user = userService.getUserByUsername(request.username) ?: throw UserNotFound("User not found")
        val ott = OTT(
            token = Uuid.random().toHexString(),
            expiry = Instant.now().plus(ottTtl, ChronoUnit.MINUTES),
            clientId = user.clientId,
            used = false
        )
        redisTemplate.opsForValue().set(getKey(ott.username), ott.tokenValue, ottTtl, TimeUnit.MINUTES)
        return ott
    }

    override fun consume(authenticationToken: OneTimeTokenAuthenticationToken): OneTimeToken? {
        val token = redisTemplate.opsForValue().get(getKey(authenticationToken.name)) ?: return null
        redisTemplate.delete(getKey(authenticationToken.name))
        val user = userService.getUserByClientId(authenticationToken.name) ?: throw UserNotFound("User not found")
        return OTT(token = token, clientId = user.username, expiry = Instant.now(), used = true)
    }

    private fun getKey(username: String) = username.plus("_ott")
}