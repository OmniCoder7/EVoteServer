package com.voting.authservice.ott

import com.voting.authservice.model.TokenType
import com.voting.authservice.service.ForgotPasswordService
import com.voting.authservice.service.RegisterService
import org.springframework.stereotype.Component

@Component
class OneTimeTokenValidationHandlerFactory(
    private val registerService: RegisterService,
    private val forgotPasswordService: ForgotPasswordService
) {
    operator fun invoke(tokenType: TokenType): OneTimeTokenValidationHandler {
        return when(tokenType) {
            TokenType.REGISTER -> registerService
            TokenType.PASSWORD_RESET -> forgotPasswordService
        }
    }
}