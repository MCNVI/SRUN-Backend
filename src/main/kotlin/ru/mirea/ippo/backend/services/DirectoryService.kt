package ru.mirea.ippo.backend.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.mirea.ippo.backend.entities.DbLecturerType
import ru.mirea.ippo.backend.models.LecturerType
import ru.mirea.ippo.backend.models.LecturerTypeTemplate
import ru.mirea.ippo.backend.repositories.LecturerTypesRepository

@Service
class DirectoryService(val lecturerTypesRepository: LecturerTypesRepository) {
    fun getLecturerTypes(): List<LecturerType>{
        return lecturerTypesRepository.findAll().map { it.toModel() }
    }

    @Transactional
    fun updateLecturerTypes(types: List<LecturerTypeTemplate>): List<LecturerType>{
        lecturerTypesRepository.saveAll(types.map { DbLecturerType.fromTemplate(it) })
        return lecturerTypesRepository.findAll().map { it.toModel() }
    }

}