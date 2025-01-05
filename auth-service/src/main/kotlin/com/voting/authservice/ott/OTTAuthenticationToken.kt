package com.voting.authservice.ott

import com.voting.authservice.model.TokenType
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken

data class OTTAuthenticationToken(
    private val token: String,
    private val userName: String,
    val tokenType: TokenType
): OneTimeTokenAuthenticationToken(
    userName,
    token
)
