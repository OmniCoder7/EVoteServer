package com.ethyllium.authservice.exception

class EmailExists(
    email: String
): Exception() {
    override val message: String = "Email $email already exists"
}