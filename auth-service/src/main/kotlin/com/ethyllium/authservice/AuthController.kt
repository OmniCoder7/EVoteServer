package com.ethyllium.authservice

import com.ethyllium.authservice.exception.EmailExists
import com.ethyllium.authservice.exception.UsernameExists
import com.ethyllium.authservice.model.*
import com.ethyllium.authservice.service.AuthService
import com.ethyllium.authservice.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService, private val userService: UserService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java.simpleName)

    @PostMapping("/register")
    fun signUpCandidate(@RequestBody signUpRequest: SignUpRequest): ResponseEntity<String> = try {
        val user = authService.registerUser(signUpRequest.toUser())
        logger.info("User ${user.userName} registered successfully")
        val token = authService.generateToken(user.userName, user.id)
        val header = HttpHeaders().apply {
            set(HttpHeaders.AUTHORIZATION, "Bearer ${token.opaqueToken}")
            set("Refresh-Token", "Bearer ${token.refreshToken}")
        }
        logger.info("Token sent to ${user.username} is $token")
        ResponseEntity.ok().headers(header).build()
    } catch (e: IllegalArgumentException) {
        logger.error(e.message)
        ResponseEntity.badRequest().body(e.message)
    } catch (e: DuplicateKeyException) {
        ResponseEntity.status(HttpStatus.CONFLICT).body("Username ${signUpRequest.username} is already taken")
    } catch (e: Exception) {
        logger.error(e.message)
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.message)
    } catch (e: EmailExists) {
        logger.error(e.message)
        ResponseEntity.status(HttpStatus.CONFLICT).body("Email ${signUpRequest.email} is already taken")
    } catch (e: UsernameExists) {
        logger.error(e.message)
        ResponseEntity.status(HttpStatus.CONFLICT).body("Username ${signUpRequest.username} is already taken")
    }

    @PostMapping("/login")
    fun signIn(@RequestBody signInRequest: SignInRequest): ResponseEntity<SignInResponse> {
        logger.info("Logging in user ${signInRequest.email}")
        val user =
            userService.getUserByEmail(signInRequest.email) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build()
        if (!authService.isPasswordValid(signInRequest.password, user.hashedPassword)) return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        ).build()
        val token = authService.generateToken(user.userName, user.id)
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer $token").body(SignInResponse(user = user))
    }

    @PostMapping("/authenticate")
    fun authenticate(@RequestHeader(HttpHeaders.AUTHORIZATION) refreshToken: String): ResponseEntity<User> {
        logger.info("Authenticating user")
        val user = userService.getUserByRefreshToken(refreshToken.substringAfter("Bearer ")) ?: run {
            logger.info("User for $refreshToken not found")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        var token = userService.getTokenByRefreshToken(refreshToken.substringAfter("Bearer ")) ?: run {
            logger.info("Token for $refreshToken not found")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        token = authService.updateToken(token)
        logger.info("User ${user.userName} authenticated successfully")
        val header = HttpHeaders().apply {
            set(HttpHeaders.AUTHORIZATION, token.accessToken)
            set("Refresh-Token", token.refreshToken)
        }
        return ResponseEntity.ok().headers(header).body(user)
    }

    data class SignInResponse(
        val user: User,
        val requiresTwoFactor: Boolean = false,
        val verificationId: String? = null,
        val requiresPasswordReset: Boolean = false,
        val blockedUntil: Long? = null,
    )
}