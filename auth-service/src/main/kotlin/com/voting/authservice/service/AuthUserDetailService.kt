package com.voting.authservice.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class AuthUserDetailService(
    private val userService: UserService,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        return userService.getUserByUsername(username)
    }
}