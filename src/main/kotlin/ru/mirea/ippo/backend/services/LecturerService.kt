package ru.mirea.ippo.backend.services

import io.ebean.annotation.Transactional
import org.springframework.stereotype.Service
import ru.mirea.ippo.backend.models.Lecturer
import ru.mirea.ippo.backend.models.LecturerTemplate
import ru.mirea.ippo.backend.repositories.LecturerRepository
import java.util.*

@Service
class LecturerService(val lecturerRepository: LecturerRepository) {

    @Transactional
    fun create(lecturerTemplate: LecturerTemplate): Lecturer = lecturerRepository.create(lecturerTemplate)

    fun findAll(): List<Lecturer> = lecturerRepository.getLecturers()
    fun find(id: UUID): Lecturer = lecturerRepository.find(id)

    @Transactional
    fun update(id: UUID, lecturerTemplate: LecturerTemplate): Lecturer = lecturerRepository.update(id, lecturerTemplate)

    @Transactional
    fun delete(id: UUID) = lecturerRepository.delete(id)

}