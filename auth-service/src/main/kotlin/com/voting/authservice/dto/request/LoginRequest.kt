package com.voting.authservice.dto.request

data class LoginRequest(
    val email: String,
    val password: String,
    val deviceInfo: String
)