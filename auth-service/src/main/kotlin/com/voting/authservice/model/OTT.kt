package com.voting.authservice.model

import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import org.springframework.security.authentication.ott.OneTimeToken
import java.time.Instant
import java.time.temporal.ChronoUnit

data class OTT(
    val token: String = "",
    private val clientId: String = "",
    val expiry: Instant = Instant.now().plus(5, ChronoUnit.MINUTES),
    val used: Boolean = false,
    @Enumerated(EnumType.STRING)
    val tokenType: TokenType = TokenType.REGISTER
): OneTimeToken{
    override fun getTokenValue(): String = token
    override fun getUsername(): String = clientId
    override fun getExpiresAt(): Instant = expiry
}

enum class TokenType(val successRedirectionUrl: String) {
    REGISTER("auth/register/verify"),
    PASSWORD_RESET("auth/password/reset")
}