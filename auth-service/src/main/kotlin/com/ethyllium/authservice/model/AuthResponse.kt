package com.ethyllium.authservice.model

data class AuthResponse(val token: String, val user: Any, val requiresVerification: Boolean)
