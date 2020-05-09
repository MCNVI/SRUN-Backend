package ru.mirea.ippo.backend.repositories

import ru.mirea.ippo.backend.database.entities.DbUser
import java.util.*

interface UserRepository : CustomJpaRepository<DbUser, UUID> {
    fun findByUsername(username: String): DbUser?
}
