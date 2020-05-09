package ru.mirea.ippo.backend.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.mirea.ippo.backend.errorhandling.Error
import ru.mirea.ippo.backend.repositories.UserRepository
import java.time.Instant


@Component
class UserService : UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        val customUser = userRepository.findByUsername(username)
        if (customUser!=null) {
            return User.builder()
                .username(customUser.username)
                .password(customUser.password)
                .roles(*customUser.roles)
                .build()
        }
        throw UsernameNotFoundException("User not found")
    }
}


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Configuration
class SecurityConfig(val objectMapper: ObjectMapper) : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var userDetailsService: UserService

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder(10)


    override fun configure(http: HttpSecurity) {
        http.cors().and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers( "/api/v2/api-docs",
                "/api/configuration/**",
                "/api/swagger*/**",
                "/api/webjars/**").permitAll()
            .antMatchers("/api/login").permitAll()
            .anyRequest().authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint { request, response, authException ->
                val route = request.requestURI?.toString()
                val error = Error(listOf(authException.message ?: "Unknown auth error"), Instant.now(), route = route)
                response.status = when (authException) {
                    is InsufficientAuthenticationException -> HttpStatus.UNAUTHORIZED.value()
                    else -> HttpStatus.FORBIDDEN.value()
                }
                response.writer.write(objectMapper.writeValueAsString(error))
            }
            .and()
            .addFilter(JWTAuthorizationFilter(authenticationManager()))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(builder: AuthenticationManagerBuilder) {
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder())
    }

//    @Bean
//    fun authenticationProvider(): DaoAuthenticationProvider? {
//        val provider = DaoAuthenticationProvider()
//        provider.setPasswordEncoder(passwordEncoder())
//        provider.setUserDetailsService(userDetailsService)
//        return provider
//    }
}