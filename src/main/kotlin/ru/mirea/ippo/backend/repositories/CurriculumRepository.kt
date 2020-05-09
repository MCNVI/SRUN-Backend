package ru.mirea.ippo.backend.repositories

import ru.mirea.ippo.backend.database.entities.DbCurriculumUnit
import java.util.*

interface CurriculumRepository : CustomJpaRepository<DbCurriculumUnit, UUID> {
    fun findAllByCurriculumId(curriculumId: UUID): List<DbCurriculumUnit>
}