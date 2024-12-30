package com.voting.authservice.validator

import com.voting.authservice.validator.annotation.ValidPassword
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PasswordValidator: ConstraintValidator<ValidPassword, String> {

    companion object {
        private const val MIN_LENGTH = 8
        private const val MAX_LENGTH = 128
        private const val MIN_DIGITS = 1
        private const val MIN_SPECIAL = 1
        private const val MIN_UPPERCASE = 1
        private const val MIN_LOWERCASE = 1
        private val SPECIAL_CHARS = """!@#$%^&*(),.?":{}|<>[]""".toSet()
        private val COMMON_PASSWORDS = setOf(
            "password", "password123", "12345678", "qwerty123",
            "letmein", "admin123", "welcome123"
        )
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) {
            addConstraintViolation(context, "Password cannot be null")
            return false
        }

        context.disableDefaultConstraintViolation()
        val violations = mutableListOf<String>()

        if (value.length < MIN_LENGTH) {
            violations.add("Password must be at least $MIN_LENGTH characters long")
        }
        if (value.length > MAX_LENGTH) {
            violations.add("Password cannot be longer than $MAX_LENGTH characters")
        }

        val digits = value.count { it.isDigit() }
        val uppercase = value.count { it.isUpperCase() }
        val lowercase = value.count { it.isLetter() && it.isLowerCase() }
        val special = value.count { it in SPECIAL_CHARS }

        if (digits < MIN_DIGITS) {
            violations.add("Password must contain at least $MIN_DIGITS digit(s)")
        }
        if (uppercase < MIN_UPPERCASE) {
            violations.add("Password must contain at least $MIN_UPPERCASE uppercase letter(s)")
        }
        if (lowercase < MIN_LOWERCASE) {
            violations.add("Password must contain at least $MIN_LOWERCASE lowercase letter(s)")
        }
        if (special < MIN_SPECIAL) {
            violations.add("Password must contain at least $MIN_SPECIAL special character(s)")
        }

        if (hasRepeatingCharacters(value)) {
            violations.add("Password cannot contain repeated characters (e.g., 'aaa', '111')")
        }
        if (hasSequentialCharacters(value)) {
            violations.add("Password cannot contain sequential characters (e.g., 'abc', '123')")
        }
        if (value.lowercase() in COMMON_PASSWORDS) {
            violations.add("Password is too common, please choose a more unique password")
        }

        violations.forEach { addConstraintViolation(context, it) }

        return violations.isEmpty()
    }

    private fun hasRepeatingCharacters(value: String): Boolean {
        return value.windowed(3).any { it[0] == it[1] && it[1] == it[2] }
    }

    private fun hasSequentialCharacters(value: String): Boolean {
        val sequences = listOf(
            "abcdefghijklmnopqrstuvwxyz",
            "0123456789"
        )

        return sequences.any { sequence ->
            val valueToCheck = value.lowercase()
            sequence.windowed(3).any { it in valueToCheck }
        }
    }

    private fun addConstraintViolation(context: ConstraintValidatorContext, message: String) {
        context.buildConstraintViolationWithTemplate(message)
            .addConstraintViolation()
    }
}