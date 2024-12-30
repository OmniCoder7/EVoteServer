package com.voting.authservice.validator

import com.voting.authservice.validator.annotation.ValidEmail
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class EmailValidator : ConstraintValidator<ValidEmail, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) {
            addConstraintViolation(context, "Password cannot be null")
            return false
        }

        context.disableDefaultConstraintViolation()
        val violations = mutableListOf<String>()

        if (value.isEmpty()) {
            violations.add("Request body is empty")
        }

        if (value.length < 3 || value.length > 254) {
            violations.add("Request body length must be between 3 and 254")
        }

        if (!value.contains("@") || value.count { it == '@' } > 1) {
            violations.add("Request body must contain exactly one '@'")
        }

        val parts = value.split("@")
        val localPart = parts[0]
        val domainPart = parts[1]

        if (!isValidLocalPart(localPart)) {
            violations.add("Invalid local part")
        }

        if (!isValidDomainPart(domainPart)) {
            violations.add("Invalid domain part")
        }
        violations.forEach { addConstraintViolation(context, it) }

        return violations.isEmpty()
    }

    private fun isValidLocalPart(localPart: String): Boolean {
        if (localPart.isEmpty() || localPart.length > 64) {
            return false
        }

        val localPartRegex = Regex("""^[a-zA-Z0-9!#$%&'*+\-/=?^_`{|}~.]+$""")
        if (!localPart.matches(localPartRegex)) {
            return false
        }

        if (localPart.contains("..")) {
            return false
        }

        if (localPart.startsWith(".") || localPart.endsWith(".")) {
            return false
        }

        return true
    }

    private fun isValidDomainPart(domainPart: String): Boolean {
        if (domainPart.isEmpty() || domainPart.length > 255) {
            return false
        }

        val labels = domainPart.split(".")

        if (labels.size < 2) {
            return false
        }

        val labelRegex = Regex("""^[a-zA-Z0-9-]+$""")
        for (label in labels) {
            // Check length (1-63 characters)
            if (label.isEmpty() || label.length > 63) {
                return false
            }

            if (!label.matches(labelRegex)) {
                return false
            }

            if (label.startsWith("-") || label.endsWith("-")) {
                return false
            }
        }

        return !labels.last().all { it.isDigit() }
    }

    private fun addConstraintViolation(context: ConstraintValidatorContext, message: String) {
        context.buildConstraintViolationWithTemplate(message)
            .addConstraintViolation()
    }
}