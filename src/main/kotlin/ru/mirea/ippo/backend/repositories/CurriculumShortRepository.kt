package ru.mirea.ippo.backend.repositories

import ru.mirea.ippo.backend.database.entities.DbCurriculumShort

interface CurriculumShortRepository : CustomJpaRepository<DbCurriculumShort, String> {
    fun existsByFieldOfStudyAndEducationalProfileAndStartYear(field: String, profile: String, startYear: Int): Boolean
}