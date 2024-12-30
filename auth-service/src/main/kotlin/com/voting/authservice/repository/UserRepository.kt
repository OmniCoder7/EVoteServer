package com.voting.authservice.repository

import com.voting.authservice.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, String> {
    @Modifying
    @Query("update User u set u.isUserEnabled = true where u.clientId = ?1")
    fun enableUser(clientId: String)
    fun findByClientId(clientId: String): MutableList<User>
    fun findByEmail(email: String): MutableList<User>
}