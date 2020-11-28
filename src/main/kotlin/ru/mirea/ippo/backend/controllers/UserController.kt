package ru.mirea.ippo.backend.controllers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
import ru.mirea.ippo.backend.security.SecurityConstants
import java.util.*

@RestController
@CrossOrigin
class UserController() {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @PostMapping("login")
    fun login(@RequestBody creds: Creds): ResponseEntity<Any> {
        val authReq = UsernamePasswordAuthenticationToken(creds.username, creds.password)
        try {
            val auth = authenticationManager.authenticate(authReq)
            val token: String = JWT.create()
                .withSubject((auth?.principal as User).username)
                .withClaim("Role", (auth.principal as User).authorities.elementAt(0).authority)
                .withExpiresAt(Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SecurityConstants.JWT_SECRET))

            return ResponseEntity.ok()
                .body(
                    AuthResponse(
                        (auth.principal as User).username,
                        token,
                        (auth.principal as User).authorities.elementAt(0).authority
                    )
                )
        }
        catch (ex: BadCredentialsException){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header("denied","denied")
                .body("denied")
        }
    }

    @PostMapping("logout")
    fun logout() {
        SecurityContextHolder.clearContext()
    }
}

data class Creds(val username: String, val password: String)

data class AuthResponse(val username: String, val bearer: String, val role: String)