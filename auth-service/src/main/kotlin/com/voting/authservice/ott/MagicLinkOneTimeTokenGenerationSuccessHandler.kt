package com.voting.authservice.ott

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.ott.OneTimeToken
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler
import org.springframework.security.web.util.UrlUtils
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class MagicLinkOneTimeTokenGenerationSuccessHandler : OneTimeTokenGenerationSuccessHandler {
    override fun handle(request: HttpServletRequest, response: HttpServletResponse, oneTimeToken: OneTimeToken) {
        val builder =
            UriComponentsBuilder.fromUriString(UrlUtils.buildFullRequestUrl(request)).replacePath(request.contextPath)
                .replaceQuery(null).fragment(null).path("/login/ott").queryParam("token", oneTimeToken.tokenValue)
        val magicLink = builder.toUriString()

        // RedirectOneTimeTokenGenerationSuccessHandler(specific page's link).handle(request, response, oneTimeToken)
        // use above code to redirect to the user to a specific page after generating the token
    }
}