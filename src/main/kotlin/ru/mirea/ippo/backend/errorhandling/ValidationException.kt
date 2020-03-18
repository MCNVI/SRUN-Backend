package ru.mirea.ippo.backend.errorhandling

import org.springframework.http.HttpStatus

class ValidationException(errors: List<String>)
    : ApiException(HttpStatus.BAD_REQUEST, errors)

fun validate(vararg pairs: Pair<Boolean, String>) {
    val filtered = pairs.filter { it.first }

    if (filtered.isNotEmpty()) {
        throw ValidationException(filtered.map { it.second })
    }
}