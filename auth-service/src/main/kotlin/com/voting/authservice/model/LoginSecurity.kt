package com.voting.authservice.model

import jakarta.persistence.Embeddable
import java.time.Instant

@Embeddable
data class LoginSecurity(
    val deviceInfo: String = "",
    val ipAddress: String = "",
    val failureReason: String? = "",
    val attemptedAt: Instant = Instant.now(),
    val userAgent: String = ""
)
