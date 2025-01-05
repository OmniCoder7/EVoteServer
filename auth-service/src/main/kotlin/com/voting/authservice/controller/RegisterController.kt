package com.voting.authservice.controller

import com.voting.authservice.client.OTTClient
import com.voting.authservice.dto.request.RegisterRequest
import com.voting.authservice.dto.response.Response
import com.voting.authservice.model.TokenType
import com.voting.authservice.service.RegisterService
import com.voting.authservice.utils.ATTRIBUTE
import jakarta.servlet.http.HttpSession
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
    private val oTTClient: OTTClient,
) {

    @PostMapping("/initiateRegistration")
    fun register(@RequestBody registerRequest: RegisterRequest, session: HttpSession): ResponseEntity<Response> {
        val registerResponse =
            registerService.register(registerRequest.copy(password = passwordEncoder.encode(registerRequest.password)))
        session.setAttribute(ATTRIBUTE.USERNAME_ATTRIBUTE, registerResponse.first.username)
        session.setAttribute(ATTRIBUTE.TOKEN_TYPE_ATTRIBUTE, TokenType.REGISTER.name)
        oTTClient.sendToken(registerRequest.email)
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.AUTHORIZATION, registerResponse.second)
            .body(Response.Success(registerResponse.first))
    }

    @GetMapping("/verify-email/{username}")
    fun resendOTT(@PathVariable username: String, session: HttpSession): ResponseEntity<String> {
        val user = registerService.findByUsername(username) ?: return ResponseEntity.notFound().build()
        session.setAttribute(ATTRIBUTE.USERNAME_ATTRIBUTE, username)
        session.setAttribute(ATTRIBUTE.TOKEN_TYPE_ATTRIBUTE, TokenType.REGISTER.name)
        oTTClient.sendToken(user.username)
        return ResponseEntity.ok("One Time Token sent successfully")
    }
}