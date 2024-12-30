package com.voting.authservice.ott

import com.voting.authservice.model.TokenType
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken

data class OTTAuthenticationToken(
    private val token: String,
    val clientId: String,
    val tokenType: TokenType
): OneTimeTokenAuthenticationToken(
    clientId,
    token
)
