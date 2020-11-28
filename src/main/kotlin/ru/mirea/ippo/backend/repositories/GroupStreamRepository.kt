package ru.mirea.ippo.backend.repositories

import ru.mirea.ippo.backend.database.entities.DbGroupStream
import java.util.*

interface GroupStreamRepository :CustomJpaRepository<DbGroupStream, UUID> {
    fun deleteByStreamIdAndGroupCode(streamId: UUID, groupCode: String)
}