package ru.mirea.ippo.backend.models

import java.util.*

data class CustomUser(
    val id: UUID? = null,
    val username: String,
    val password: String,
    val roles: Array<String>? = null
)