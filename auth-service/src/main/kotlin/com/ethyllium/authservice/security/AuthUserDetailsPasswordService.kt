package com.ethyllium.authservice.security

import com.ethyllium.authservice.model.User
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsPasswordService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class AuthUserDetailsPasswordService(
    private val mongoTemplate: MongoTemplate,
) : UserDetailsPasswordService {
    override fun updatePassword(user: UserDetails?, newPassword: String?): UserDetails {
        mongoTemplate.updateFirst(
            Query(Criteria.where(User::email.name).`is`(user?.username)),
            Update.update(User::hashedPassword.name, newPassword),
            User::class.java
        )
        return mongoTemplate.findOne(
            Query(Criteria.where(User::email.name).`is`(user?.username)), User::class.java
        ) ?: throw UsernameNotFoundException("User not found")
    }
}