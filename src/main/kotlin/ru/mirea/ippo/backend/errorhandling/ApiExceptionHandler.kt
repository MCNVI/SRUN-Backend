package ru.mirea.ippo.backend.errorhandling

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.Instant

fun getRoute(request: WebRequest): String? =
    (request as? ServletWebRequest)?.request?.requestURI?.toString()

@RestControllerAdvice
@Component
class ApiExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleExceptionInternal(ex: Exception, body: Any?, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        return ResponseEntity
            .status(status)
            .headers(headers)
            .body(Error(listOf(ex.message ?: "Unknown server error"), Instant.now(), body, getRoute(request)))
    }

    @ExceptionHandler(ApiException::class)
    fun handleApiException(e: ApiException, request: WebRequest): ResponseEntity<Error> {
        if(e.logException) log.error("Error stack trace:", e)
        return ResponseEntity.status(e.status).body(e.format(getRoute(request)))
    }

    @ExceptionHandler(Throwable::class)
    fun handleOtherException(t: Throwable, request: WebRequest): ResponseEntity<Error> {
        val rse = t as? ResponseStatusException
        val responseStatus = rse?.status ?: HttpStatus.INTERNAL_SERVER_ERROR
        val responseCause = rse?.reason ?: t.message ?: "Unknown internal server error"
        log.error("Error stack trace: ", t)

        return ResponseEntity
            .status(responseStatus)
            .body(Error(listOf(responseCause), Instant.now(), route = getRoute(request)))
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApiExceptionHandler::class.java)
    }
}