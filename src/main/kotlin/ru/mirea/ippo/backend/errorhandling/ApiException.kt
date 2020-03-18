package ru.mirea.ippo.backend.errorhandling

import org.springframework.http.HttpStatus
import java.time.Instant

data class Error(val cause: List<String>, val timestamp: Instant, val body: Any? = null, val route: String? = null) {
    val version = "2"
}

open class ApiException(val status: HttpStatus, private val _message: List<String>, val logException: Boolean = false) : Exception() {
    constructor(status: HttpStatus, _message: String, logException: Boolean = false) : this(status, listOf(_message),logException)

    open fun format(route: String? = null): Error = Error(_message, Instant.now(), route = route)
}