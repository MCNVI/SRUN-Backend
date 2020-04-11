package ru.mirea.ippo.backend.repositories

import ru.mirea.ippo.backend.database.entities.DbStream
import java.util.*

interface StreamRepository : CustomJpaRepository<DbStream, UUID> {
    fun findAllByCurriculumIdAndSubjectAndCourse(curriculumId: UUID, subject: String, course: Int): List<DbStream>
}