package com.voting.authservice.config

import com.voting.authservice.federation.FederatedIdentityAuthenticationSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.session.HttpSessionEventPublisher

@Configuration
class SecurityConfig(
    private val userDetailsService: UserDetailsService
) {

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests{authorize ->
                authorize
                    .requestMatchers("/auth/**").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { formLogin ->
                formLogin
                    .loginPage("/login")
            }
            .oauth2Login { oauth2Login ->
                oauth2Login
                    .loginPage("/login")
                    .successHandler(authenticationSuccessHandler())
            }
            .build()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(): AuthenticationProvider = DaoAuthenticationProvider().apply {
        setPasswordEncoder(passwordEncoder())
        setUserDetailsService(userDetailsService)
    }

    @Bean
    fun authenticationManager(): AuthenticationManager = AuthenticationManager { authentication ->
        authenticationProvider().authenticate(authentication)
    }

    // @formatter:on
    @Bean
    fun sessionRegistry(): SessionRegistry {
        return SessionRegistryImpl()
    }

    @Bean
    fun httpSessionEventPublisher(): HttpSessionEventPublisher {
        return HttpSessionEventPublisher()
    }

    // @formatter:on
    private fun authenticationSuccessHandler(): AuthenticationSuccessHandler {
        return FederatedIdentityAuthenticationSuccessHandler()
    }
}