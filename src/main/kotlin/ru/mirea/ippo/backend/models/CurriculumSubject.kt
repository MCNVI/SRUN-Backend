package ru.mirea.ippo.backend.models


data class CurriculumSubject (
    val course: Short,
    val semester: Short,
    val subject: String,
    val curriculumUnits: List<CurriculumUnit>
)