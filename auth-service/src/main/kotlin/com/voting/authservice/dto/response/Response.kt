package com.voting.authservice.dto.response

sealed interface Response {
    data class Success<T>(val data: T) : Response
    data class Error(val message: String) : Response
}