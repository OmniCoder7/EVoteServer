package com.ethyllium.authservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.ErrorResponse

class JWTException(status: HttpStatus, errorResponse: ErrorResponse) : BaseException(status, errorResponse)

data class BaseException(
    val status: HttpStatus,
    val errorResponse: ErrorResponse
): Exception()