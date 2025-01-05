package com.voting.authservice.ott

import com.voting.authservice.model.TokenType
import com.voting.authservice.utils.ATTRIBUTE
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.stereotype.Component

@Component
class OneTimeTokenAuthenticationConverter(
) : AuthenticationConverter {
    override fun convert(request: HttpServletRequest?): OneTimeTokenAuthenticationToken? {
        if (request == null) return null
        val token = request.session.getAttribute(ATTRIBUTE.OTT_TOKEN_ATTRIBUTE) as String
        val username = request.session.getAttribute(ATTRIBUTE.USERNAME_ATTRIBUTE) as String
        val tokenType = TokenType.getToken(request.session.getAttribute(ATTRIBUTE.TOKEN_TYPE_ATTRIBUTE) as String)
        return OTTAuthenticationToken(token = token, userName = username, tokenType = tokenType)
    }
}