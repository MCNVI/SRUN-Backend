package ru.mirea.ippo.backend.models

import java.math.BigDecimal

data class Form13LoadUnit(
    val subject: String,
    val course: Int,
    val semester: String,
    val groups: String,
    val lectureStreamNumber: Int,
    val practicalStreamNumber: Int,
    val laboratoryStreamNumber: Int,
    val studentsNumber: Int,
    val lectureLoad: BigDecimal?,
    val practicalClassLoad: BigDecimal?,
    val laboratoryWorkLoad: BigDecimal?,
    val testLoad: BigDecimal?,
    val examLoad: BigDecimal?,
    val courseWorkLoad: BigDecimal?,
    val courseProjectLoad: BigDecimal?,
    val lecturer: String
)