package com.voting.authservice.exception

data class UserNotFound(override val message: String) : RuntimeException(message)