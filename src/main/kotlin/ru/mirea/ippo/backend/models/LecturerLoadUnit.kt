package ru.mirea.ippo.backend.models

import java.math.BigDecimal

data class LecturerLoadUnit(
    val subject: String,
    val groups: String,
    val studentsNumber: Int,
    val lectureLoad: BigDecimal?,
    val practicalClassLoad: BigDecimal?,
    val laboratoryWorkLoad: BigDecimal?,
    val testLoad: BigDecimal?,
    val examLoad: BigDecimal?,
    val courseWorkLoad: BigDecimal?,
    val courseProjectLoad: BigDecimal?
) {
//    companion object{
//        fun fromLoadUnit
//    }
}