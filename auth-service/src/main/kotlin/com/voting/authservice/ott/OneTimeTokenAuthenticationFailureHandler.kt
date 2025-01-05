package com.voting.authservice.ott

import com.voting.authservice.model.TokenType
import com.voting.authservice.service.RedisService
import com.voting.authservice.utils.KeyUtils
import com.voting.authservice.utils.ATTRIBUTE
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class OneTimeTokenAuthenticationFailureHandler(
    private val redisService: RedisService,
    private val oneTimeTokenValidationHandlerFactory: OneTimeTokenValidationHandlerFactory
) : AuthenticationFailureHandler {

    override fun onAuthenticationFailure(
        request: HttpServletRequest, response: HttpServletResponse, exception: AuthenticationException
    ) {
        val tokenType = TokenType.getToken(request.session.getAttribute(ATTRIBUTE.TOKEN_TYPE_ATTRIBUTE) as String)
        val username = request.session.getAttribute(ATTRIBUTE.USERNAME_ATTRIBUTE) as String
        request.session.invalidate()
        redisService.increase(KeyUtils.getOTTRateKey(username, tokenType.name))
        oneTimeTokenValidationHandlerFactory(tokenType).failure("Authentication failed", username)
    }
}
