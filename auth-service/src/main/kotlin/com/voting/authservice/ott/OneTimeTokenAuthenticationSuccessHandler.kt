package com.voting.authservice.ott

import com.voting.authservice.model.TokenType
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OneTimeTokenAuthenticationSuccessHandler : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication
    ) {
        // getting email in authentication.name
        response.setHeader(HttpHeaders.AUTHORIZATION, request.getParameter("client-id"))
        val tokenType = when (request.getParameter("token-type")) {
            TokenType.REGISTER.name -> TokenType.REGISTER
            TokenType.PASSWORD_RESET.name -> TokenType.PASSWORD_RESET
            else -> throw IllegalArgumentException("Invalid token type")
        }
        request.getRequestDispatcher(tokenType.successRedirectionUrl).forward(request, response)
    }
}