package com.ethyllium.authservice.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails
import java.util.Collections

@Document(collection = "User")
data class User(
    @MongoId(value = FieldType.OBJECT_ID)
    val id: String = ObjectId.get().toHexString(),
    @Indexed(unique = true)
    val userName: String,
    var hashedPassword: String,
    @Indexed(unique = true)
    val email: String,
    val roles: List<String>,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        Collections.unmodifiableCollection(AuthorityUtils.createAuthorityList(roles.map { it }))

    override fun getPassword(): String = hashedPassword

    override fun getUsername(): String = userName
}