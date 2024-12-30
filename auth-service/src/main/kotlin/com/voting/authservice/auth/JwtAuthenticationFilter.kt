package com.voting.authservice.auth

import com.voting.authservice.utils.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authHeader = request.getHeader("Authorization")
            if (authHeader.isNullOrEmpty() || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response)
                return
            }

            val jwtToken = authHeader.substringAfter("Bearer ").trim()
            val email = jwtService.extractEmail(jwtToken)

            if (email != null && SecurityContextHolder.getContext().authentication == null) {
                val foundUser = userDetailsService.loadUserByUsername(email)

                if (jwtService.isValid(jwtToken, foundUser)) {
                    updateContext(foundUser, request)
                } else {
                    logger.warn("Invalid JWT token for email: $email")
                }
            }
        } catch (e: Exception) {
            logger.error("Error processing JWT authentication: ${e.message}", e)
        }

        filterChain.doFilter(request, response)
    }
}

private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
    val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
    SecurityContextHolder.getContext().authentication = authToken
}