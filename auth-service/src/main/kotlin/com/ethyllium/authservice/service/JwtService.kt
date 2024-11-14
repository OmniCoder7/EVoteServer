package com.ethyllium.authservice.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey


@ConfigurationProperties(prefix = "jwt")
@Component
class JwtProperties {
    companion object {
        var secret: String = "7Y8F6KljgXTN+e0KvXq7F3PPhkox5/onR+XnhsbMTvJ4FzHfDnMS5msx4x0l2Dmi7H6E0glH9XVyi+SBaAz0Rw=="
        var accessTokenExpiration: Long = 300000
        var refreshTokenExpiration: Long = 10 * 24 * 60 * 60 * 1000
    }
}

@Service
class JwtService {

    fun extractUsername(token: String): String? {
        return extractClaim(token, claimsResolver = { obj: Claims -> obj.subject })
    }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver.invoke(claims)
    }

    fun generateAccessToken(username: String): String {
        return generateAccessToken(HashMap(), username)
    }

    fun generateAccessToken(extraClaims: Map<String?, Any?>, username: String): String {
        return buildToken(extraClaims, username, getExpirationTime())
    }

    fun getExpirationTime(): Long {
        return JwtProperties.accessTokenExpiration
    }

    private fun buildToken(
        extraClaims: Map<String?, Any?>, username: String, expiration: Long
    ): String {
        return Jwts.builder().claims(extraClaims).subject(username).issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiration)).signWith(getSignInKey(), Jwts.SIG.HS256)
            .compact()
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username) && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String): Date {
        return extractClaim(token) { obj: Claims -> obj.expiration }
    }

    private fun extractAllClaims(token: String?): Claims {
        return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).payload
    }

    private fun getSignInKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(JwtProperties.secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}