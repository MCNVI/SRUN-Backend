package ru.mirea.ippo.backend.services

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.mirea.ippo.backend.database.entities.DbLecturer
import ru.mirea.ippo.backend.errorhandling.ObjectNotFoundException
import ru.mirea.ippo.backend.models.Lecturer
import ru.mirea.ippo.backend.models.LecturerTemplate
import ru.mirea.ippo.backend.models.PrioritizedLecturer
import ru.mirea.ippo.backend.repositories.DirectoryRepository
import ru.mirea.ippo.backend.repositories.LecturerRepository
import ru.mirea.ippo.backend.repositories.LoadRepository
import java.math.BigDecimal
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class LecturerService(val lecturerRepository: LecturerRepository, val directoryRepository: DirectoryRepository, val loadRepository: LoadRepository) {

    fun find(id: UUID): Lecturer =
        lecturerRepository.findByIdOrNull(id)?.toModel() ?: throw ObjectNotFoundException("Lecturer", id)

    fun findAll(): List<Lecturer> = lecturerRepository.findAll().map { it.toModel() }

    fun getByRelevance(loadUnitId: UUID): List<PrioritizedLecturer> {
        val loadUnit = loadRepository.findByIdOrNull(loadUnitId)?.toModel() ?: throw ObjectNotFoundException("LoadUnit", loadUnitId)
        
    }

    fun createOrUpdate(lecturerTemplate: LecturerTemplate): Lecturer {
        val lecturerType = directoryRepository.findByIdOrNull(lecturerTemplate.lecturerTypeId)
            ?: throw ObjectNotFoundException("LecturerType", lecturerTemplate.lecturerTypeId)
        val lecturerLoadForRate =
            BigDecimal.valueOf(lecturerTemplate.monthAmount.toDouble() / 10 * lecturerTemplate.lecturerRate.toDouble() * lecturerType.studyLoad)
        val lecturerMaxLoadForRate =
            BigDecimal.valueOf(lecturerTemplate.monthAmount.toDouble() / 10 * lecturerTemplate.lecturerRate.toDouble() * 900)
        val dbLecturer = DbLecturer.fromTemplate(lecturerTemplate, lecturerLoadForRate, lecturerMaxLoadForRate)

        val lecturer = lecturerRepository.save(dbLecturer)
        lecturerRepository.refresh(lecturer)

        return lecturerRepository.findByIdOrNull(lecturer.id)?.toModel() ?: throw ObjectNotFoundException(
            "Lecturer",
            lecturer.id
        )
    }

    fun delete(id: UUID) {
        if (lecturerRepository.findByIdOrNull(id) != null)
            lecturerRepository.deleteById(id)
        else throw ObjectNotFoundException("Lecturer", id)
    }

}