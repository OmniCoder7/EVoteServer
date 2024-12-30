package com.voting.authservice.exception

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<Any> {
        val errors = StringBuilder()
        e.constraintViolations.forEach { violation -> errors.append(violation.message).append("\n") }
        return ResponseEntity(errors.toString(), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MissingHeaderException::class)
    fun handleMissingHeaderException(ex: MissingHeaderException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }

    @ExceptionHandler(UserNotFound::class)
    fun handleUserNotFoundException(ex: UserNotFound): ResponseEntity<String> {
        return ResponseEntity.notFound().build()
    }
}