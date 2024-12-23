package com.voting.authservice.federation

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import java.io.IOException
import java.util.function.Consumer

// end::imports[]
class FederatedIdentityAuthenticationSuccessHandler : AuthenticationSuccessHandler {
    private val delegate: AuthenticationSuccessHandler = SavedRequestAwareAuthenticationSuccessHandler()

    private var oauth2UserHandler =
        Consumer { _: OAuth2User? -> }

    private var oidcUserHandler =
        Consumer { user: OidcUser -> oauth2UserHandler.accept(user) }

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        if (authentication is OAuth2ClientAuthenticationToken) {
            if (authentication.principal is OidcUser) {
                oidcUserHandler.accept(authentication.principal as OidcUser)
            } else if (authentication.principal is OAuth2User) {
                oauth2UserHandler.accept(authentication.principal as OAuth2User)
            }
        }

        delegate.onAuthenticationSuccess(request, response, authentication)
    }

    fun setOAuth2UserHandler(oauth2UserHandler: Consumer<OAuth2User?>) {
        this.oauth2UserHandler = oauth2UserHandler
    }

    fun setOidcUserHandler(oidcUserHandler: Consumer<OidcUser>) {
        this.oidcUserHandler = oidcUserHandler
    }
} // end::class[]