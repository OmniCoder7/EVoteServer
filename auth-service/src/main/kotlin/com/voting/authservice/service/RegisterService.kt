package com.voting.authservice.service

import com.voting.authservice.dto.request.RegisterRequest
import com.voting.authservice.dto.response.RegisterResponse
import com.voting.authservice.model.RefreshToken
import com.voting.authservice.model.User
import com.voting.authservice.ott.OneTimeTokenValidationHandler
import com.voting.authservice.repository.UserRepository
import com.voting.authservice.utils.JwtService
import com.voting.authservice.utils.KeyUtils
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Service
class RegisterService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val redisService: RedisService
): OneTimeTokenValidationHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @OptIn(ExperimentalUuidApi::class)
    fun register(@Valid registerRequest: RegisterRequest): Pair<RegisterResponse, String> {
        if (userRepository.findByEmail(registerRequest.email).firstOrNull() != null) {
            logger.error("User with email ${registerRequest.email} already exists")
            throw IllegalArgumentException("User with email ${registerRequest.email} already exists")
        }
        val user = User(
            name = registerRequest.name,
            email = registerRequest.email,
            passwordHash = registerRequest.password,
            roles = mutableSetOf("Voter"),
            isUserEnabled = false,
            clientId = Uuid.random().toHexString()
        )

        val refreshToken = RefreshToken(
            refreshToken = jwtService.generateRefreshToken(registerRequest.email),
            deviceInfo = registerRequest.deviceInfo
        )
        refreshToken.user = user
        user.refreshToken = refreshToken
        userRepository.save(user)
        val accessToken = jwtService.generateAccessToken(username = user.email)
        redisService.saveEncrypted(
            KeyUtils.getAccessTokenKey(user.clientId),
            accessToken,
            Duration.of(jwtService.accessTokenExpiration, ChronoUnit.MILLIS),
            purpose = RedisService.ACCESS_TOKEN_PURPOSE
        )
        logger.info("User with email ${registerRequest.email} registered successfully")
        return Pair(RegisterResponse(clientId = user.clientId, username = user.username), accessToken)
    }

    @Transactional
    fun verify(username: String) {
        val user = userRepository.findByUsername(username).firstOrNull()
            ?: throw IllegalArgumentException("User with username $username not found while verification success")
        userRepository.enableUser(user.clientId)
        logger.info("User with email ${user.email} verified successfully")
    }

    fun findByUsername(username: String) = userRepository.findByUsername(username).firstOrNull()
    override fun success(data: String) {
        TODO("Not yet implemented")
    }

    override fun failure(reason: String, data: String?) {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun generate(): String {
        return Uuid.random().toHexString()
    }
}