package com.voting.authservice.ott

import com.voting.authservice.exception.MissingHeaderException
import com.voting.authservice.model.TokenType
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.stereotype.Component

@Component
class OneTimeTokenAuthenticationConverter(
) : AuthenticationConverter {
    override fun convert(request: HttpServletRequest?): OneTimeTokenAuthenticationToken? {
        if (request == null) return null
        val token = request.getParameter("token") ?: throw MissingHeaderException("Token header is missing")
        val clientId = request.getParameter("client-id") ?: throw MissingHeaderException("client-id header is missing")
        val tokenType =
            when (request.getParameter("token-type") ?: throw MissingHeaderException("Token-type header is missing")) {
                TokenType.REGISTER.name -> TokenType.REGISTER
                TokenType.PASSWORD_RESET.name -> TokenType.PASSWORD_RESET
                else -> throw IllegalArgumentException("Invalid token type")
            }
        return OTTAuthenticationToken(token = token, clientId = clientId, tokenType = tokenType)
    }
}