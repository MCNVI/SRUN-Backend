package ru.mirea.ippo.backend.models

import java.util.*

data class CurriculumShort (
    val id: UUID?,
    val fieldOfStudy: String,
    val educationalProfile: String,
    val startYear: Int
)