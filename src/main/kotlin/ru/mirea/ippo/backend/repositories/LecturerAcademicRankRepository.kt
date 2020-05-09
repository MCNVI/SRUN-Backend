package ru.mirea.ippo.backend.repositories

import ru.mirea.ippo.backend.database.entities.DbLecturerAcademicRank
import ru.mirea.ippo.backend.database.entities.DbLecturerDegree
import ru.mirea.ippo.backend.database.entities.DbLecturerType
import java.util.*

interface LecturerAcademicRankRepository : CustomJpaRepository<DbLecturerAcademicRank, String>

