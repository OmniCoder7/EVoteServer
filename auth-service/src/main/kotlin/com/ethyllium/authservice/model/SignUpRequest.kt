package com.ethyllium.authservice.model

data class SignUpRequest(
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val role: String
)

fun SignUpRequest.toUser() = User(
    userName = username,
    hashedPassword = password,
    email = email,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    roles = listOf(role)
)