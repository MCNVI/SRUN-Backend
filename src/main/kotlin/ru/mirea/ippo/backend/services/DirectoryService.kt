package ru.mirea.ippo.backend.services

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.mirea.ippo.backend.database.entities.DbLecturerType
import ru.mirea.ippo.backend.errorhandling.ObjectNotFoundException
import ru.mirea.ippo.backend.models.LecturerAcademicRank
import ru.mirea.ippo.backend.models.LecturerDegree
import ru.mirea.ippo.backend.models.LecturerType
import ru.mirea.ippo.backend.models.LecturerTypeTemplate
import ru.mirea.ippo.backend.repositories.LecturerAcademicRankRepository
import ru.mirea.ippo.backend.repositories.LecturerDegreeRepository
import ru.mirea.ippo.backend.repositories.LecturerTypeRepository
import java.util.*

@Service
class DirectoryService(
    val lecturerTypeRepository: LecturerTypeRepository,
    val lecturerDegreeRepository: LecturerDegreeRepository,
    val lecturerAcademicRankRepository: LecturerAcademicRankRepository
) {

    fun getLecturerTypes(): List<LecturerType> = lecturerTypeRepository.findAll().map { it.toModel() }

    fun updateLecturerTypes(types: List<LecturerTypeTemplate>): List<LecturerType> {
        val list = lecturerTypeRepository.saveAll(types.map { DbLecturerType.fromTemplate(it) })
        lecturerTypeRepository.refreshAll(list)
        return lecturerTypeRepository.findAll().map { it.toModel() }
    }

    fun deleteLecturerType(typeId: UUID) {
        if (lecturerTypeRepository.findByIdOrNull(typeId) != null)
            lecturerTypeRepository.deleteById(typeId)
        else throw ObjectNotFoundException("LecturerType", typeId)
    }

    fun getLecturerDegrees(): List<LecturerDegree> {
        return lecturerDegreeRepository.findAll().map { it.toModel() }
    }

    fun getLecturerAcademicRanks(): List<LecturerAcademicRank> {
        return lecturerAcademicRankRepository.findAll().map { it.toModel() }
    }

}