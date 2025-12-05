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
    fun register(@RequestBody user: UserAccount): String {
        if (userRepo.findByUsername(user.username) != null) {
            return "User already exists"
        }

        userRepo.save(user)
        return "User registered"
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
