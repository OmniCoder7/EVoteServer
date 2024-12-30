package com.voting.authservice.repository

import com.voting.authservice.model.RefreshToken
import com.voting.authservice.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository: JpaRepository<RefreshToken, String> {
    fun findByUser(user: User): MutableList<RefreshToken>

    @Modifying
    @Query("UPDATE RefreshToken r SET r.refreshToken = :refreshToken WHERE r.refreshTokenId = :refreshTokenId")
    fun updateToken(refreshToken: String, refreshTokenId: String)
}