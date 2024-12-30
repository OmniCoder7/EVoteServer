package com.voting.authservice.exception

data class MissingHeaderException(override val message: String) : RuntimeException(message)
