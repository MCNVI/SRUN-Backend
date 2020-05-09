package ru.mirea.ippo.backend.repositories

import ru.mirea.ippo.backend.database.entities.DbLecturerType
import java.util.*

interface LecturerTypeRepository : CustomJpaRepository<DbLecturerType, UUID>

