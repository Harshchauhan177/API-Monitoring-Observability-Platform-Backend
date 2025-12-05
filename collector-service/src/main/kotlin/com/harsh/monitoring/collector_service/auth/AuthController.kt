package com.harsh.monitoring.collector_service.auth

import com.harsh.monitoring.collector_service.models.metadata.UserAccount
import com.harsh.monitoring.collector_service.repositories.metadata.UserAccountRepository
import org.springframework.web.bind.annotation.*

data class LoginRequest(val username: String, val password: String)

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepo: UserAccountRepository,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): Map<String, String> {
        val user = userRepo.findByUsername(req.username)

        if (user == null || user.passwordHash != req.password) {
            return mapOf("error" to "Invalid username or password")
        }

        val token = jwtUtil.generateToken(req.username)
        return mapOf("token" to token)
    }

    @PostMapping("/register")
    fun register(@RequestBody req: LoginRequest): String {
        val user = UserAccount(
            username = req.username,
            passwordHash = req.password
        )
        userRepo.save(user)
        return "User registered"
    }
}
