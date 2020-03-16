package ru.mirea.ippo.backend.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ru.mirea.ippo.backend.entities.DbLecturerType

interface LecturerTypeRepository : JpaRepository<DbLecturerType, String> {
}