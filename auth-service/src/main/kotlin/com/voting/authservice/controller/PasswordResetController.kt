package com.voting.authservice.controller

import com.voting.authservice.service.ForgotPasswordService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/password-reset")
class PasswordResetController(private val forgotPasswordService: ForgotPasswordService) {

    @GetMapping("/{username}")
    fun sendResetToken(@PathVariable("username") username: String) {
        forgotPasswordService.sendResetPasswordEmail(username)
    }
}