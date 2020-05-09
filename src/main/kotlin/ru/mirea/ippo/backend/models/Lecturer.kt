package ru.mirea.ippo.backend.models

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Lecturer(
    val id: UUID,
    val lecturerType: LecturerType?,
    val academicRank: LecturerAcademicRank?,
    val degree: LecturerDegree?,
    val name: String,
    val middleName: String,
    val lastName: String,
    val lecturerRate: BigDecimal,
    val lecturerLoadForRate: BigDecimal,
    val lecturerMaxLoadForRate: BigDecimal,
    val employmentStartDate: Instant,
    val employmentFinishDate: Instant,
    val monthAmount: BigDecimal
) {
    fun getFio(): String{
        return "$lastName $name $middleName"
    }
    fun getForm(): String{
        if (lecturerType == null){
            return "Неизвестная формы устройства"
        }
        else if (!lecturerType.isPartTime) {
            return "Штатный"
        }
        else if (lecturerType.isPartTime && lecturerType.isExternal){
            return "Внешний совместитель"
        }
        else return "Внутренний совместитель"
    }
}

data class LecturerTemplate(
    val id: UUID?,
    val lecturerTypeId: UUID,
    val lecturerType: LecturerTypeTemplate?,
    val academicRank: LecturerAcademicRank?,
    val degree: LecturerDegree?,
    val name: String,
    val middleName: String,
    val lastName: String,
    val lecturerRate: BigDecimal,
    val employmentStartDate: Instant,
    val employmentFinishDate: Instant,
    val monthAmount: BigDecimal
)

data class PrioritizedLecturer(
    val id: UUID,
    val lecturerType: LecturerType?,
    val name: String,
    val middleName: String,
    val lastName: String,
    val lecturerRate: BigDecimal,
    val lecturerLoadForRate: BigDecimal,
    val lecturerMaxLoadForRate: BigDecimal,
    val employmentStartDate: Instant,
    val employmentFinishDate: Instant,
    val monthAmount: BigDecimal
)