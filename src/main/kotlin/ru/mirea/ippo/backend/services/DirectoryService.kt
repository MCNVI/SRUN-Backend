package ru.mirea.ippo.backend.services

import io.ebean.annotation.Transactional
import org.springframework.stereotype.Service
import ru.mirea.ippo.backend.models.LecturerType
import ru.mirea.ippo.backend.models.LecturerTypeTemplate
import ru.mirea.ippo.backend.repositories.DirectoryRepository
import java.util.*

@Service
class DirectoryService(val directoryRepository: DirectoryRepository) {

    fun getLecturerTypes(): List<LecturerType> = directoryRepository.getLecturersTypes()

    @Transactional
    fun updateLecturerTypes(types: List<LecturerTypeTemplate>): List<LecturerType> =
        directoryRepository.updateLecturersTypes(types)

    @Transactional
    fun deleteLecturerType(type: UUID): List<LecturerType> = directoryRepository.deleteLecturerType(type)

}