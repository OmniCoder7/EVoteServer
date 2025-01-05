package com.voting.authservice.model

import com.voting.authservice.service.RedisService
import org.springframework.security.authentication.ott.OneTimeToken
import java.time.Instant
import java.time.temporal.ChronoUnit

data class OTT(
    private val userName: String = "",
    val expiry: Instant = Instant.now().plus(5, ChronoUnit.MINUTES),
    val tokenType: TokenType,
    val token: String,
) : OneTimeToken {
    override fun getTokenValue(): String = token
    override fun getUsername(): String = userName
    override fun getExpiresAt(): Instant = expiry
}

enum class TokenType(val purpose: String) {
    REGISTER(RedisService.REGISTER_PURPOSE), PASSWORD_RESET(RedisService.PASSWORD_PURPOSE);

    companion object {
        fun getToken(tokenName: String) = when (tokenName) {
            REGISTER.name -> REGISTER
            PASSWORD_RESET.name -> PASSWORD_RESET
            else -> throw IllegalArgumentException("Invalid token type")
        }
    }
}