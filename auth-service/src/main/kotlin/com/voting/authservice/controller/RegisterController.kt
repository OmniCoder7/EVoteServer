package com.voting.authservice.controller

import com.voting.authservice.client.OTTClient
import com.voting.authservice.dto.request.RegisterRequest
import com.voting.authservice.dto.response.Response
import com.voting.authservice.model.TokenType
import com.voting.authservice.service.RegisterService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth/register")
class RegisterController(
    private val registerService: RegisterService,
    private val passwordEncoder: PasswordEncoder,
    private val oTTClient: OTTClient
) {

    @PostMapping("/start")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<Response> {
        val registerResponse =
            registerService.register(registerRequest.copy(password = passwordEncoder.encode(registerRequest.password)))
        oTTClient.sendToken(registerRequest.email)
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.AUTHORIZATION, registerResponse.second)
            .body(Response.Success(registerResponse.first))
    }

    @PostMapping("/verify-email/{token}")
    fun verify(@PathVariable token: String, @RequestParam clientId: String) {
        oTTClient.submit(token, clientId, TokenType.REGISTER.name)
    }

    @GetMapping("/auth/register/verify")
    fun verifyEmail(@RequestHeader(HttpHeaders.AUTHORIZATION) clientId: String): ResponseEntity<String> {
        registerService.verify(clientId)
        return ResponseEntity.ok("Email verified successfully")
    }

    @GetMapping("/verify-email/{clientId}")
    fun resendOTT(@PathVariable clientId: String): ResponseEntity<String> {
        val user = registerService.findByClientId(clientId) ?: return ResponseEntity.notFound().build()
        oTTClient.sendToken(user.username)
        return ResponseEntity.ok("One Time Token sent successfully")
    }
}