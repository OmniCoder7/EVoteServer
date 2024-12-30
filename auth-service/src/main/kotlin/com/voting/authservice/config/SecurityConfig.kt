package com.voting.authservice.config

import com.voting.authservice.ott.MagicLinkOneTimeTokenGenerationSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AnonymousAuthenticationProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationProvider
import org.springframework.security.authentication.ott.OneTimeTokenService
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun defaultSecurityFilterChain(
        http: HttpSecurity,
        oneTimeTokenGenerationSuccessHandler: MagicLinkOneTimeTokenGenerationSuccessHandler,
        oneTimeTokenService: OneTimeTokenService,
        authenticationFailureHandler: AuthenticationFailureHandler,
        authenticationConverter: AuthenticationConverter,
        authenticationSuccessHandler: AuthenticationSuccessHandler
    ): SecurityFilterChain {
        return http.cors { it.disable() }.csrf { it.disable() }.authorizeHttpRequests { authorize ->
            authorize.anyRequest().permitAll()//.requestMatchers("auth/**").authenticated()
        }.formLogin { it.permitAll() }.oneTimeTokenLogin { ott ->
            ott
                .tokenGeneratingUrl("/ott/token")
                .tokenGenerationSuccessHandler(oneTimeTokenGenerationSuccessHandler)
                .defaultSubmitPageUrl("/ott/submit").tokenService(oneTimeTokenService)
                .authenticationSuccessHandler(authenticationSuccessHandler)
                .authenticationFailureHandler(authenticationFailureHandler)
                .authenticationConverter(authenticationConverter)
        }.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun daoAuthenticationProvider(userDetailsService: UserDetailsService): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder())
        return provider
    }

    @Bean
    fun anonymousAuthenticationProvider(): AnonymousAuthenticationProvider {
        val provider = AnonymousAuthenticationProvider("anonymous")
        return provider
    }

    @Bean
    fun oneTimeTokenAuthenticationProvider(
        oneTimeTokenService: OneTimeTokenService, userDetailsService: UserDetailsService
    ): OneTimeTokenAuthenticationProvider {
        val provider = OneTimeTokenAuthenticationProvider(oneTimeTokenService, userDetailsService)
        return provider
    }

    @Bean
    fun authenticationManager(vararg authenticationProviders: AuthenticationProvider): AuthenticationManager {
        return ProviderManager(authenticationProviders.toList())
    }
}