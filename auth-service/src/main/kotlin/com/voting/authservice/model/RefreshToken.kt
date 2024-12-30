package com.voting.authservice.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import org.hibernate.envers.Audited
import org.springframework.security.authentication.ott.OneTimeToken
import java.time.Instant

@Entity
@Audited
data class RefreshToken(
    @Id
    @UuidGenerator
    val refreshTokenId: String = "",
    var refreshToken: String = "",
    val deviceInfo: String = "",
    val revokedAt: Instant? = null,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null
)