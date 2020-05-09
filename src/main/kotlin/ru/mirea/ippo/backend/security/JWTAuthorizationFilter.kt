package ru.mirea.ippo.backend.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(authenticationManager: AuthenticationManager?) :
    BasicAuthenticationFilter(authenticationManager) {

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authentication = getAuthentication(request)
        if (authentication == null) {
            filterChain.doFilter(request, response)
            return
        }
        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.getHeader(SecurityConstants.TOKEN_HEADER)
        if (!token.isNullOrEmpty() && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            val parsedToken = JWT.require(Algorithm.HMAC512(SecurityConstants.JWT_SECRET))
                .build()
                .verify(token.replace("Bearer ", ""))
            val username = parsedToken.subject
            println(parsedToken.claims.getValue("Role").asString())
            return UsernamePasswordAuthenticationToken(username, null,
                listOf(GrantedAuthority { parsedToken.claims.getValue("Role").asString() })
            )

        }
        return null
    }

}