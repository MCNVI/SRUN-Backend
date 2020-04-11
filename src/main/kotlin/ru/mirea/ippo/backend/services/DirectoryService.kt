package ru.mirea.ippo.backend.services

import io.ebean.annotation.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.mirea.ippo.backend.database.entities.DbLecturerType
import ru.mirea.ippo.backend.errorhandling.ObjectNotFoundException
import ru.mirea.ippo.backend.models.LecturerType
import ru.mirea.ippo.backend.models.LecturerTypeTemplate
import ru.mirea.ippo.backend.repositories.DirectoryRepository
import java.util.*

@Service
class DirectoryService(val directoryRepository: DirectoryRepository) {

    fun getLecturerTypes(): List<LecturerType> = directoryRepository.findAll().map { it.toModel() }

    fun updateLecturerTypes(types: List<LecturerTypeTemplate>): List<LecturerType> {
        val list = directoryRepository.saveAll(types.map { DbLecturerType.fromTemplate(it) })
        directoryRepository.refreshAll(list)
        return directoryRepository.findAll().map { it.toModel() }
    }

    fun deleteLecturerType(typeId: UUID) {
        if (directoryRepository.findByIdOrNull(typeId) != null)
            directoryRepository.deleteById(typeId)
        else throw ObjectNotFoundException("LecturerType", typeId)
    }
}