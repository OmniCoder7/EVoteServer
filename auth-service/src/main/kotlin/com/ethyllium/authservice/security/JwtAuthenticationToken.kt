package com.ethyllium.authservice.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class JwtAuthenticationToken(
    private val principal: UserDetails, private val credentials: Any?, authorities: Collection<GrantedAuthority>
) : AbstractAuthenticationToken(authorities) {
    override fun getCredentials(): Any? = credentials
    override fun getPrincipal(): Any = this.principal
}