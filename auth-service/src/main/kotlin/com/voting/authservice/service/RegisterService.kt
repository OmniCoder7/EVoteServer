package com.voting.authservice.service

import com.voting.authservice.dto.request.RegisterRequest
import com.voting.authservice.dto.response.RegisterResponse
import com.voting.authservice.model.RefreshToken
import com.voting.authservice.model.User
import com.voting.authservice.repository.UserRepository
import com.voting.authservice.utils.JwtService
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.temporal.ChronoUnit

@Service
class RegisterService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val redisService: RedisService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

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
        )

        val refreshToken = RefreshToken(
            refreshToken = jwtService.generateRefreshToken(registerRequest.email),
            deviceInfo = registerRequest.deviceInfo
        )
        refreshToken.user = user
        user.refreshToken = refreshToken
        userRepository.save(user)
        val accessToken = jwtService.generateAccessToken(username = user.username)
        redisService.save(user.clientId, accessToken, Duration.of(jwtService.accessTokenExpiration, ChronoUnit.MILLIS))
        logger.info("User with email ${registerRequest.email} registered successfully")
        return Pair(RegisterResponse(user.clientId), accessToken)
    }

    @Transactional
    fun verify(clientId: String) {
        val user = userRepository.findByClientId(clientId).firstOrNull()
            ?: throw IllegalArgumentException("User with clientId $clientId not found")
        userRepository.enableUser(user.clientId)
        logger.info("User with email ${user.username} verified successfully")
    }

    fun findByClientId(clientId: String) = userRepository.findByClientId(clientId).firstOrNull()
}