package ru.mirea.ippo.backend.errorhandling

import org.springframework.http.HttpStatus

class CreatingNotAllowedException(objectName: String?, reason: String)
    : ApiException(HttpStatus.FORBIDDEN, "Failed to create object \"${objectName ?: "unknown object"}\": $reason")