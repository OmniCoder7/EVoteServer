package com.voting.authservice.ott

import com.voting.authservice.exception.UserNotFoundException
import com.voting.authservice.model.OTT
import com.voting.authservice.model.TokenType
import com.voting.authservice.service.RedisService
import com.voting.authservice.service.UserService
import com.voting.authservice.utils.KeyUtils
import com.voting.authservice.utils.ATTRIBUTE
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest
import org.springframework.security.authentication.ott.OneTimeToken
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken
import org.springframework.security.authentication.ott.OneTimeTokenService
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class OTTService(
    private val redisService: RedisService,
    @Value("\${ott.ttl}") private val ottTtl: Long,
    private val userService: UserService,
    private val oneTimeTokenValidationHandlerFactory: OneTimeTokenValidationHandlerFactory,
    private val httpServletRequest: HttpServletRequest
) : OneTimeTokenService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun generate(request: GenerateOneTimeTokenRequest): OneTimeToken {
        logger.info("Generating new One Time Token for user : ${request.username}")
        val user = userService.getUserByUsername(request.username) ?: run {
            throw UserNotFoundException("User not found for username : ${request.username} while generating OTT")
        }
        val tokenType = TokenType.getToken(httpServletRequest.session.getAttribute(ATTRIBUTE.TOKEN_TYPE_ATTRIBUTE) as String)
        val token = oneTimeTokenValidationHandlerFactory(tokenType).generate()
        httpServletRequest.session.setAttribute(ATTRIBUTE.OTT_TOKEN_ATTRIBUTE, token)
        val ott = OTT(
            expiry = Instant.now().plus(ottTtl, ChronoUnit.MINUTES),
            userName = user.username,
            tokenType = tokenType,
            token = token
        )
        redisService.saveEncrypted(
            KeyUtils.getOTTKey(ott.username), ott.tokenValue, Duration.ofMinutes(ottTtl), tokenType.purpose
        )
        return ott
    }

    override fun consume(authenticationToken: OneTimeTokenAuthenticationToken): OneTimeToken? {
        val authToken = authenticationToken as OTTAuthenticationToken
        val username = authToken.principal as String
        val tokenType = authToken.tokenType
        val token = redisService.getEncryptedValue(KeyUtils.getOTTKey(username), tokenType.purpose) ?: return null
        if (token == authToken.tokenValue) {
            val rate = redisService.getEncryptedValue(
                KeyUtils.getOTTRateKey(
                    username, authToken.tokenType.name
                ), tokenType.purpose
            )?.toInt() ?: 0
            if (rate > 3) return null
            logger.info("Consuming One Time Token for user : $username")
            redisService.delete(KeyUtils.getOTTKey(username))
            val user = userService.getUserByUsername(username) ?: run {
                throw UserNotFoundException("User not found for username : $username while consuming OTT")
            }
            return OTT(
                token = token, userName = user.username, expiry = Instant.now(), tokenType = authToken.tokenType
            )
        }
        return null
    }
}