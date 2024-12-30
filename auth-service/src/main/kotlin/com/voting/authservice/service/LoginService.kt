package com.voting.authservice.service

import com.voting.authservice.dto.request.LoginRequest
import com.voting.authservice.model.RefreshToken
import com.voting.authservice.repository.RefreshTokenRepository
import com.voting.authservice.repository.UserRepository
import com.voting.authservice.utils.JwtService
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class LoginService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun login(loginRequest: LoginRequest): String {
        val user = userRepository.findByEmail(loginRequest.email).firstOrNull()
            ?: throw UsernameNotFoundException("User with email ${loginRequest.email} not found")
        if (passwordEncoder.matches(loginRequest.password, user.password).not()) {
            throw IllegalArgumentException("Invalid password")
        }
        val accessToken = jwtService.generateAccessToken(user.username)
        val refreshToken = RefreshToken(
           refreshToken = jwtService.generateRefreshToken(user.username), deviceInfo = loginRequest.deviceInfo
        )
        refreshTokenRepository.save(refreshToken)
        return accessToken
    }

    fun getRefreshToken(userDetails: UserDetails): String {
        val user = userRepository.findByEmail(userDetails.username).firstOrNull() ?: run {
            logger.info("User with email ${userDetails.username} not found")
            throw UsernameNotFoundException("User with email ${userDetails.username} not found")
        }
        val refreshToken = refreshTokenRepository.findByUser(user).firstOrNull() ?: run {
            logger.info("Refresh token not found")
            throw IllegalStateException("Refresh token not found")
        }
        if (passwordEncoder.matches(user.password, user.password).not()) {
            logger.info("Invalid refresh token")
            throw IllegalArgumentException("Invalid refresh token")
        }
        val expiration = Instant.ofEpochMilli(jwtService.getClaim(refreshToken.refreshToken, "exp") as Long)
        if (expiration.isBefore(Instant.now())) {
            logger.info("Refresh token expired")
            refreshToken.refreshToken = jwtService.generateRefreshToken(user.username)
            refreshTokenRepository.updateToken(refreshToken.refreshToken, refreshToken.refreshTokenId)
        }
        return refreshToken.refreshToken
    }


}
