package ru.mirea.ippo.backend.repositories

import ru.mirea.ippo.backend.database.entities.DbGroup

interface GroupRepository : CustomJpaRepository<DbGroup, String> {
    fun findAllByCodeNotIn(bannedGroupCodes: List<String>): List<DbGroup>
}