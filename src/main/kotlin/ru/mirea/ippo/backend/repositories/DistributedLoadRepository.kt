package ru.mirea.ippo.backend.repositories

import ru.mirea.ippo.backend.database.entities.DbLecturer
import ru.mirea.ippo.backend.database.entities.DbLoadUnitDistributedPart
import java.util.*

interface DistributedLoadRepository : CustomJpaRepository<DbLoadUnitDistributedPart, UUID> {
    fun findAllByLoadUnitId(id: UUID): List<DbLoadUnitDistributedPart>
    fun findAllByLecturerId(lecturerId: UUID): List<DbLoadUnitDistributedPart>
}