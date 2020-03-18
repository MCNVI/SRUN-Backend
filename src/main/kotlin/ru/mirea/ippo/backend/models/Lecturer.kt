package ru.mirea.ippo.backend.models

import java.math.BigDecimal
import java.util.*

data class Lecturer(
    val id: UUID,
    val lecturerType: LecturerType,
    val name: String,
    val middleName: String,
    val lastName: String,
    val lecturerRate: BigDecimal,
    val lecturerHoursForRate: BigDecimal,
    val lecturerMaxHoursForRate: BigDecimal
)

data class LecturerTemplate(
    val id: UUID?,
    val lecturerTypeId: UUID,
    val name: String,
    val middleName: String,
    val lastName: String,
    val lecturerRate: BigDecimal,
    val lecturerHoursForRate: BigDecimal,
    val lecturerMaxHoursForRate: BigDecimal
)