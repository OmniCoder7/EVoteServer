package com.voting.authservice.service

import com.voting.authservice.utils.JwtService
import org.bouncycastle.math.ec.rfc8032.Ed448.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.encrypt.AesBytesEncryptor.CipherAlgorithm
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Duration
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.*

@Service
class RedisService(
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${redis.register.secretKey}") private val registerKey: String,
    @Value("\${redis.password.secretKey}") private val passwordKey: String,
    @Value("\${jwt.secret}") private val jwtSecret: String
) {

    companion object {
        const val REGISTER_PURPOSE = "registration"
        const val PASSWORD_PURPOSE = "passwordReset"
        const val ACCESS_TOKEN_PURPOSE = "access_token"
    }

    fun saveEncrypted(key: String, value: Any, ttl: Duration, purpose: String) {
        try {
            val secretKey = getSecretKey(purpose)
            val cipher = Cipher.getInstance("AES")
            val keySpec = SecretKeySpec(secretKey, "AES")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            val encryptedBytes = cipher.doFinal(value.toString().toByteArray(StandardCharsets.UTF_8))
            val encryptedValue = Base64.getEncoder().encodeToString(encryptedBytes)
            redisTemplate.opsForValue().set(key, encryptedValue, ttl)
        } catch (e: Exception) {
            throw RuntimeException("Error encrypting and saving value in Redis", e)
        }
    }

    fun getEncryptedValue(key: String, purpose: String): String? {
        try {
            val storedValue = redisTemplate.opsForValue().get(key) ?: return null

            val secretKey = getSecretKey(purpose)

            val cipher = Cipher.getInstance("AES")
            val keySpec = SecretKeySpec(secretKey, "AES")
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            val decodedValue = Base64.getDecoder().decode(storedValue)
            val decryptedBytes = cipher.doFinal(decodedValue)
            return String(decryptedBytes, StandardCharsets.UTF_8)

        } catch (e: Exception) {
            // Handle exceptions (e.g., log, re-throw)
            throw RuntimeException("Error retrieving and decrypting value from Redis", e)
        }
    }

    fun save(key: String, value: Any, ttl: Duration) {
        redisTemplate.opsForValue().set(key, value.toString(), ttl)
    }

    fun get(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    fun delete(key: String): Boolean {
        return redisTemplate.delete(key)
    }

    fun isKeyPresent(token: String): Boolean {
        return redisTemplate.hasKey(token)
    }

    fun increase(key: String, value: Long = 1): Long {
        return redisTemplate.opsForValue().increment(key, value) ?: 0
    }

    private fun getSecretKey(purpose: String): ByteArray {
        return when (purpose) {
            REGISTER_PURPOSE -> registerKey.toByteArray()
            PASSWORD_PURPOSE -> passwordKey.toByteArray()
            ACCESS_TOKEN_PURPOSE -> jwtSecret.toByteArray()
            else -> throw IllegalArgumentException("Invalid purpose: $purpose")
        }
    }
}
