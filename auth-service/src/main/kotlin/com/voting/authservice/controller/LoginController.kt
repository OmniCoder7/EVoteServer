package com.voting.authservice.controller

import com.voting.authservice.dto.request.LoginRequest
import com.voting.authservice.dto.response.Response
import com.voting.authservice.service.LoginService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class LoginController(
    private val loginService: LoginService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Response> {
        try {
            val token = loginService.login(loginRequest)
            logger.info("User with email ${loginRequest.email} logged in")
            val header = HttpHeaders().apply { set("Authorization", "Bearer $token") }
            return ResponseEntity.status(HttpStatus.OK).headers(header).body(Response.Success(token))
        } catch (e: IllegalStateException) {
            logger.info("User with email ${loginRequest.email} not found")
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.Error("User with email ${loginRequest.email} not found"))
        }
    }

    @GetMapping("/refresh")
    fun refresh(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<Response> {
        try {
            val token = loginService.getRefreshToken(userDetails)
            logger.info("Token refreshed")
            val header = HttpHeaders().apply { set("Authorization", "Bearer $token") }
            return ResponseEntity.status(HttpStatus.OK).headers(header).body(Response.Success("Token refreshed"))
        } catch (e: IllegalStateException) {
            logger.info("Invalid refresh token")
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.Error("Invalid refresh token"))
        }
    }
}