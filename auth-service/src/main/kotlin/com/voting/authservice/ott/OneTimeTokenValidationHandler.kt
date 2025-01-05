package com.voting.authservice.ott

import org.springframework.stereotype.Component

@Component
interface OneTimeTokenValidationHandler {
    fun success(data: String)
    fun failure(reason: String, data: String? = null)
    fun generate(): String
}