package com.ethyllium.authservice.security

import com.ethyllium.authservice.service.JwtService
import com.ethyllium.authservice.service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
    private val userService: UserService
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.substring(7)
            try {
                val userDetails = userService.getUserByUsername(
                    jwtService.extractUsername(token) ?: throw UsernameNotFoundException("Username not found")
                ) ?: throw UsernameNotFoundException("User not found")
                val username = userDetails.username
                val user = userDetailsService.loadUserByUsername(username)

                val authentication = JwtAuthenticationToken(user, user.password, user.authorities)

                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication

            } catch (ex: Exception) {
                log.error("Error during JWT validation", ex)
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}