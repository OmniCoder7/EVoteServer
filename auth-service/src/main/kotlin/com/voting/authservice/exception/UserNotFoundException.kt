package com.voting.authservice.exception

data class UserNotFoundException(override val message: String) : RuntimeException(message)