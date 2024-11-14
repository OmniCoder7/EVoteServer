package com.ethyllium.authservice.util

object RandomStringGenerator {
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun generate(length: Int, allowSpecialChars: Boolean = false): String {
        val finalCharPool = if (allowSpecialChars) {
            charPool + "!@#$%^&*()_+-=[]{}|;:,.<>?".toList()
        } else {
            charPool
        }

        return (1..length)
            .map { finalCharPool.random() }
            .joinToString("")
    }

    fun generateWithCustomChars(length: Int, customCharPool: List<Char>): String {
        require(customCharPool.isNotEmpty()) { "Custom character pool cannot be empty" }
        return (1..length)
            .map { customCharPool.random() }
            .joinToString("")
    }
}