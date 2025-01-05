package com.voting.authservice.ott

import com.voting.authservice.model.OTT
import com.voting.authservice.utils.ATTRIBUTE
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
        val ott = oneTimeToken as OTT
        val builder =
            UriComponentsBuilder.fromUriString(UrlUtils.buildFullRequestUrl(request)).replacePath(request.contextPath)
                .queryParam(ATTRIBUTE.USERNAME_ATTRIBUTE, ott.username)
                .queryParam(ATTRIBUTE.OTT_TOKEN_ATTRIBUTE, ott.tokenValue)
                .queryParam(ATTRIBUTE.TOKEN_TYPE_ATTRIBUTE, ott.tokenType)
                .build()
        val magicLink = builder.toUriString()
    }
}