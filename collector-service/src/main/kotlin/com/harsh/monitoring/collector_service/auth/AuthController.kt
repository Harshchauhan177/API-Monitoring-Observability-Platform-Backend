package com.harsh.monitoring.collector_service.auth

import com.harsh.monitoring.collector_service.models.metadata.UserAccount
import com.harsh.monitoring.collector_service.repositories.metadata.UserAccountRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepo: UserAccountRepository,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/register")
    fun register(@RequestBody req: Map<String, String>): Map<String, String> {
        val username = req["username"] ?: return mapOf("error" to "username missing")
        val password = req["password"] ?: return mapOf("error" to "password missing")

        if (userRepo.findByUsername(username) != null) {
            return mapOf("error" to "User already exists")
        }

        if (username.isBlank()) {
            return mapOf("error" to "Username cannot be empty")
        }

        if (password.length < 3) {
            return mapOf("error" to "Password must be at least 3 characters")
        }

        val user = UserAccount(
            username = username,
            password = password,
            role = "USER"
        )

        userRepo.save(user)
        return mapOf("message" to "User registered successfully")
    }

    @PostMapping("/login")
    fun login(@RequestBody req: Map<String, String>): Map<String, String> {
        val username = req["username"] ?: return mapOf("error" to "username missing")
        val password = req["password"] ?: return mapOf("error" to "password missing")

        val user = userRepo.findByUsername(username)
            ?: return mapOf("error" to "Invalid username or password")

        if (user.password != password)
            return mapOf("error" to "Invalid username or password")

        val token = jwtUtil.generateToken(username)
        return mapOf("token" to token)
    }
}
