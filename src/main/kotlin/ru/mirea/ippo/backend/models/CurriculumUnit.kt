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
    val curriculumId: UUID,
    val department: Int
) {
    fun toKey() = CurriculumUnitSubjectKey(course,semester,subject)
}

data class CurriculumUnitSubjectKey(
    val course: Short,
    val semester: Short,
    val subject: String
)

data class CurriculumUnitTemplate(
    val id: UUID?,
    val course: Short,
    val semester: Short,
    val subject: String,
    val loadType: String,
    val load: BigDecimal?,
    val curriculumId: UUID?,
    val department: Int
)