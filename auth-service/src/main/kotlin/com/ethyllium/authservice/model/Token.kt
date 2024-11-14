package com.ethyllium.authservice.model

import com.ethyllium.authservice.service.JwtProperties
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId

@Document(collection = "Access Token")
data class Token(
    @MongoId(value = FieldType.OBJECT_ID)
    val id: String = ObjectId.get().toHexString(),
    @Indexed(unique = true)
    val accessToken: String,
    val accessTokenExpiration: Long = System.currentTimeMillis() + JwtProperties.accessTokenExpiration,
    val opaqueToken: String,
    val refreshTokenExpiration: Long = System.currentTimeMillis() + JwtProperties.refreshTokenExpiration,
    val userId: String,
    val refreshToken: String
) {
    override fun toString(): String =
        "Token(id='$id', accessToken='$accessToken', expiration=$accessTokenExpiration, opaqueToken='$opaqueToken', userId='$userId', refreshToken='$refreshToken')"

    fun isRefreshTokenExpired(): Boolean = refreshTokenExpiration < System.currentTimeMillis()
    fun isAccessTokenExpired(): Boolean = accessTokenExpiration < System.currentTimeMillis()

}
