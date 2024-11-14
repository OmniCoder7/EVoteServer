package com.ethyllium.authservice.security

import com.ethyllium.authservice.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationProvider(
    private val userService: UserService
) : AuthenticationProvider {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

    override fun authenticate(auth: Authentication): Authentication {
        try {
            val email = auth.name
            val password = auth.credentials.toString()
            val user = userService.getUserByEmail(email)
            if (user != null && password == user.password) {
                val authorities = user.authorities
                return JwtAuthenticationToken(user, password, authorities)
            } else {
                log.info("user with email address ${auth.name} doesn't exist")
                throw AuthenticationServiceException("Authentication failed")
            }
        } catch (e: BadCredentialsException) {
            throw e
        } catch (e: LockedException) {
            throw e

        } catch (e: DisabledException) {
            throw e

        } catch (e: AccountExpiredException) {
            throw e

        } catch (e: CredentialsExpiredException) {
            throw e

        }


    }
}