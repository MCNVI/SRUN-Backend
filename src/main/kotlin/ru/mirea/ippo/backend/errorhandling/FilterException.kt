package ru.mirea.ippo.backend.errorhandling

import org.springframework.http.HttpStatus

class FilterException(objectName: String?, reason: String)
    : ApiException(HttpStatus.BAD_REQUEST, "Failed to filter objects \"${objectName ?: "unknown object"}\": $reason")