package com.ethyllium.authservice.model

data class SignInRequest(
    val email: String,
    val password: String
)
