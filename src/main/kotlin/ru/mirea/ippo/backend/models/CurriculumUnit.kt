package ru.mirea.ippo.backend.models

import java.math.BigDecimal
import java.util.*

data class CurriculumUnit(
    val id: UUID?,
    val course: Short,
    val semester: Short,
    val subject: String,
    val loadType: String,
    val load: BigDecimal?,
    val curriculumId: UUID
) {
    fun toKey() = CurriculumUnitSubjectKey(course,semester,subject)
}

data class CurriculumUnitSubjectKey(
    val course: Short,
    val semester: Short,
    val subject: String
)