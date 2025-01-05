package com.voting.authservice.service

import com.voting.authservice.client.OTTClient
import com.voting.authservice.exception.UserNotFoundException
import com.voting.authservice.model.TokenType
import com.voting.authservice.ott.OneTimeTokenValidationHandler
import com.voting.authservice.repository.UserRepository
import org.springframework.stereotype.Service
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Service
class ForgotPasswordService(
    private val userRepository: UserRepository,
    private val oTTClient: OTTClient
): OneTimeTokenValidationHandler {

    fun sendResetPasswordEmail(username: String) {
        val user = userRepository.findByUsername(username).firstOrNull()
            ?: throw UserNotFoundException("User with username $username not found while trying to reset password")
        oTTClient.sendToken(username = user.username, tokenType = TokenType.PASSWORD_RESET.name)
    }

    override fun success(data: String) {
        TODO("Not yet implemented")
    }

    override fun failure(reason: String, data: String?) {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun generate(): String {
        return Uuid.random().toHexString()
    }
}