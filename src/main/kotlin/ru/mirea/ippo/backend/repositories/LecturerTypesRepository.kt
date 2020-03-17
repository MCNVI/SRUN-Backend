package ru.mirea.ippo.backend.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ru.mirea.ippo.backend.entities.DbLecturerType
import java.util.*

interface LecturerTypesRepository : JpaRepository<DbLecturerType, UUID> {
}