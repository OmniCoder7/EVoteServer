package com.voting.authservice.model

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class ServerMetadata(
    @Value("\${oauth.server.issuer}") val issuer: String,
    @Value("\${oauth.server.authorization-endpoint}") val authorizationEndpoint: String? = null,
    @Value("\${oauth.server.token-endpoint}") val tokenEndpoint: String? = null,
    @Value("\${oauth.server.jwks-uri}") val jwksUri: String? = null,
    @Value("\${oauth.server.registration-endpoint}") val registrationEndpoint: String? = null,
    @Value("\${oauth.server.token-revocation-endpoint}") val revocationEndpoint: String? = null,
    @Value("\${oauth.server.token-introspection-endpoint}") val introspectionEndpoint: String? = null
)