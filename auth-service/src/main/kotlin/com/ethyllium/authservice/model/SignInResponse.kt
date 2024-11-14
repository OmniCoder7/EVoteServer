package com.ethyllium.authservice.model

class SignInResponse(
    val username: String,
    val email: String,
    val roles: List<Role>,
    val firstName: String,
    val lastName: String
)
