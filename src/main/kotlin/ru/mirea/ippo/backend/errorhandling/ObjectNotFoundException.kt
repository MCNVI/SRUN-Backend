package ru.mirea.ippo.backend.errorhandling

import org.springframework.http.HttpStatus

class ObjectNotFoundException(objectName: String, id: Any)
    : ApiException(HttpStatus.NOT_FOUND, "Object \"${objectName}\" with id \"${id}\" not found")