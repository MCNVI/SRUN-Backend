package ru.mirea.ippo.backend.repositories

import ru.mirea.ippo.backend.database.entities.DbCurriculumShort
import java.util.*

interface CurriculumShortRepository : CustomJpaRepository<DbCurriculumShort, UUID> {
    fun existsByFieldOfStudyAndEducationalProfileAndStartYear(field: String, profile: String, startYear: Int): Boolean
}