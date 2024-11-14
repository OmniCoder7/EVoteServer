package com.ethyllium.authservice.exception

class UsernameExists(
    username: String
): Exception() {
    override val message: String = "Username $username already exists"
}