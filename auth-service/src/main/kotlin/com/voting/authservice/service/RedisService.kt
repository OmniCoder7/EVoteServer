package com.voting.authservice.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    fun save(key: String, value: Any, ttl: Duration) {
        redisTemplate.opsForValue().set(key, value.toString(), ttl)
    }

    fun deleteToken(key: String) {
        redisTemplate.delete(key)
    }

    fun isTokenPresent(token: String): Boolean {
        return redisTemplate.hasKey(token)
    }
}
