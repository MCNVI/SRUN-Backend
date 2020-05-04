package ru.mirea.ippo.backend.models

import java.math.BigDecimal
import java.util.*

data class LoadUnit (
    val id: UUID,
    val subject: String,
    val course: Int,
    val semester: Int,
    val group: Group?,
    val stream: Stream?,
    val hours: BigDecimal,
    val hoursType: String,
    val curriculumId: UUID,
    val loadUnitPartDistributedParts: List<LoadUnitDistributedPart>
) {
    fun toKey() = LoadUnitSubjectKey(curriculumId,subject)
}

data class LoadUnitSubjectKey(
    val curriculumId: UUID,
    val subject: String
)