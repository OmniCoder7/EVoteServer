package com.voting.authservice.dto.request

import com.voting.authservice.validator.annotation.ValidEmail
import com.voting.authservice.validator.annotation.ValidPassword

data class RegisterRequest(
    val name: String,
    @ValidPassword
    val password: String,
    @ValidEmail
    val email: String,
    val deviceInfo: String = "deviceInfo"
)
