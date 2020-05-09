package ru.mirea.ippo.backend.repositories

import ru.mirea.ippo.backend.database.entities.DbLoadUnit
import java.util.*

interface LoadRepository : CustomJpaRepository<DbLoadUnit, UUID> {
    fun findByDepartment(depId: Int): List<DbLoadUnit>
    fun findAllByIdIn(ids: List<UUID>): List<DbLoadUnit>
}