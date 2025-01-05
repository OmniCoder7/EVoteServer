package com.voting.authservice.service

import com.voting.authservice.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun getUserByUsername(username: String) = userRepository.findByEmail(username).firstOrNull()
}