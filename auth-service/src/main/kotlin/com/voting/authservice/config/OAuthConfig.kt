package com.voting.authservice.config

import com.nimbusds.jose.jwk.JWKSelector
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import com.voting.authservice.authentication.DeviceClientAuthenticationConverter
import com.voting.authservice.authentication.DeviceClientAuthenticationProvider
import com.voting.authservice.federation.FederatedIdentityIdTokenCustomizer
import com.voting.authservice.model.ServerMetadata
import com.voting.authservice.utils.Jwks
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher

@Configuration(proxyBeanMethods = false)
class OAuthConfig(
    private val serverMetadata: ServerMetadata
) {

    companion object {
        const val CONSENT_PAGE_URI = "/consent"
    }

    private val logger = LoggerFactory.getLogger(OAuthConfig::class.java)

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authorizationServerSecurityFilterChain(
        http: HttpSecurity,
        registeredClientRepository: RegisteredClientRepository,
        authorizationServerSettings: AuthorizationServerSettings
    ): SecurityFilterChain {
        val deviceClientAuthenticationConverter = DeviceClientAuthenticationConverter(
            authorizationServerSettings.deviceAuthorizationEndpoint
        )
        val deviceClientAuthenticationProvider = DeviceClientAuthenticationProvider(registeredClientRepository)

        val authorizationServerConfigurer: OAuth2AuthorizationServerConfigurer =
            OAuth2AuthorizationServerConfigurer.authorizationServer()
        return http.securityMatcher(authorizationServerConfigurer.endpointsMatcher)
            .with(authorizationServerConfigurer) { authorizationServer ->
                authorizationServer.deviceAuthorizationEndpoint { deviceAuthorizationEndpoint ->
                    deviceAuthorizationEndpoint.verificationUri("/activate")
                }.deviceVerificationEndpoint { deviceVerificationEndpoint ->
                    deviceVerificationEndpoint.consentPage(CONSENT_PAGE_URI)
                }.clientAuthentication { clientAuthentication ->
                    clientAuthentication.authenticationConverter(deviceClientAuthenticationConverter)
                        .authenticationProvider(deviceClientAuthenticationProvider)
                }.authorizationEndpoint { authorizationEndpoint ->
                    authorizationEndpoint.consentPage(CONSENT_PAGE_URI)
                }.oidc(Customizer.withDefaults()).authorizationServerMetadataEndpoint { metadataEndpoint ->
                        metadataEndpoint.authorizationServerMetadataCustomizer {
                            it
                                .issuer(serverMetadata.issuer)
                                .authorizationEndpoint(serverMetadata.authorizationEndpoint)
                                .tokenEndpoint(serverMetadata.tokenEndpoint)
                                .tokenRevocationEndpoint(serverMetadata.revocationEndpoint)
                                .clientRegistrationEndpoint(serverMetadata.registrationEndpoint)
                                .tokenRevocationEndpoint(serverMetadata.revocationEndpoint)
                                .build()
                        }
                    }
            }.authorizeHttpRequests { it.anyRequest().authenticated() }.exceptionHandling {
                it.defaultAuthenticationEntryPointFor(
                    LoginUrlAuthenticationEntryPoint("/auth/login"), MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
            }.build()
    }

    @Bean
    fun registeredClientRepository(jdbcTemplate: JdbcTemplate): RegisteredClientRepository {
        return JdbcRegisteredClientRepository(jdbcTemplate)
    }

    @Bean
    fun clientRegistrationRepository(jdbcTemplate: JdbcTemplate): ClientRegistrationRepository {
        return InMemoryClientRegistrationRepository(
            listOf(
                ClientRegistration.withRegistrationId("device").redirectUri("http://localhost:8080/redirection")
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE).clientId("device")
                    .authorizationUri("http://localhost:8080/authorize").tokenUri("http://localhost:8080/token")
                    .clientSecret("device").build()
            )
        )
    }

    // @formatter:on
    @Bean
    fun authorizationService(
        jdbcTemplate: JdbcTemplate?, registeredClientRepository: RegisteredClientRepository?
    ): JdbcOAuth2AuthorizationService {
        return JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository)
    }

    @Bean
    fun authorizationConsentService(
        jdbcTemplate: JdbcTemplate?, registeredClientRepository: RegisteredClientRepository?
    ): JdbcOAuth2AuthorizationConsentService {
        return JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository)
    }

    @Bean
    fun idTokenCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext> {
        return FederatedIdentityIdTokenCustomizer()
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val rsaKey: RSAKey = Jwks.generateRsa()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource { jwkSelector: JWKSelector, securityContext: SecurityContext? -> jwkSelector.select(jwkSet) }
    }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext?>?): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder().build()
    }
}