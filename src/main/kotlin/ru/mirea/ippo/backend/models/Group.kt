package ru.mirea.ippo.backend.models

import java.util.*

data class Group(
    val code: String,
    val studentsNumber: Int,
    val course: Int,
    val curriculum: CurriculumShort?,
    val curriculumId: UUID
)