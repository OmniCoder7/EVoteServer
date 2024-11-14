package com.ethyllium.authservice.service

import com.ethyllium.authservice.model.User
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthUserDetailsService(
    private val mongoTemplate: MongoTemplate
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        return mongoTemplate.findOne(Query.query(Criteria.where(User::userName.name).`is`(username)), User::class.java)
            ?: throw UsernameNotFoundException("username $username is not found")
    }
}
