package ru.mirea.ippo.backend.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.mirea.ippo.backend.models.LecturerType
import ru.mirea.ippo.backend.models.LecturerTypeTemplate
import ru.mirea.ippo.backend.repositories.DirectoryRepository

@Service
class DirectoryService(val directoryRepository: DirectoryRepository) {

    fun getLecturerTypes(): List<LecturerType> = directoryRepository.getLecturersTypes()


    @Transactional
    fun updateLecturerTypes(types: List<LecturerTypeTemplate>): List<LecturerType> {
        TODO()
    }

}