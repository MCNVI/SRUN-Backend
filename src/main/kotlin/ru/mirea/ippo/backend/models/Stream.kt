package ru.mirea.ippo.backend.models

import java.util.*

data class Stream (
    val id: UUID?,
    val type: String,
    val subject: String,
    val course: Int,
    val groups: List<Group>?,
    val curriculumId: UUID
)