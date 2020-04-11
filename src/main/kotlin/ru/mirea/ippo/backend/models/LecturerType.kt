package ru.mirea.ippo.backend.models

import java.util.*

data class LecturerType(
    val id: UUID,
    val type: String,
    val studyLoad: Int,
    val isPartTime: Boolean,
    val isExternal: Boolean
)

data class LecturerTypeTemplate(
    val id: UUID?,
    val type: String,
    val studyLoad: Int,
    val isPartTime: Boolean,
    val isExternal: Boolean?
)