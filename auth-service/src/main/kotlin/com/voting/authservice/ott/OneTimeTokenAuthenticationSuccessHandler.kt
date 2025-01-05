package com.voting.authservice.ott

import com.voting.authservice.model.TokenType
import com.voting.authservice.utils.ATTRIBUTE
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OneTimeTokenAuthenticationSuccessHandler(
    private val oneTimeTokenValidationHandlerFactory: OneTimeTokenValidationHandlerFactory
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication
    ) {
        // getting email in authentication.name
        val tokenType = TokenType.getToken(request.session.getAttribute(ATTRIBUTE.TOKEN_TYPE_ATTRIBUTE) as String)
        val oneTimeTokenValidationHandler = oneTimeTokenValidationHandlerFactory(tokenType)
        val username = request.session.getAttribute(ATTRIBUTE.USERNAME_ATTRIBUTE) as String
        synchronized(request.session) {
            request.session.invalidate()
        }
        oneTimeTokenValidationHandler.success(username)
    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authentication: Authentication
    ) {
        onAuthenticationSuccess(request, response, authentication)
    }
}