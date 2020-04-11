package ru.mirea.ippo.backend.repositories

import ru.mirea.ippo.backend.database.entities.DbLecturer
import java.util.*

interface LecturerRepository : CustomJpaRepository<DbLecturer,UUID>
