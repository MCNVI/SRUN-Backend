package ru.mirea.ippo.backend.services

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import ru.mirea.ippo.backend.database.entities.DbLoadUnitDistributedPart
import ru.mirea.ippo.backend.errorhandling.ObjectNotFoundException
import ru.mirea.ippo.backend.models.LoadUnit
import ru.mirea.ippo.backend.models.LoadUnitDistributedPart
import ru.mirea.ippo.backend.repositories.DistributedLoadRepository
import ru.mirea.ippo.backend.repositories.LoadRepository
import java.math.BigDecimal
import java.util.*

@Service
class LoadService(val loadRepository: LoadRepository, val distributedLoadRepository: DistributedLoadRepository) {

    fun findAll(): List<LoadUnit> {
        return loadRepository.findAll().map { it.toModel() }
    }

    fun findDistributedParts(id: UUID): List<LoadUnitDistributedPart> {
        return distributedLoadRepository.findAllByLoadUnitId(id).map { it.toModel() }
    }

    fun findById(id: UUID): LoadUnit {
        return loadRepository.findByIdOrNull(id)?.toModel() ?: throw ObjectNotFoundException("LoadUnit", id)
    }

    fun deleteLoadUnit(id: UUID) {
        loadRepository.deleteById(id)
    }

    fun deleteLoadUnitDistributedPart(id: UUID, partId: UUID) {
        distributedLoadRepository.deleteById(partId)
    }

    fun addOrUpdatePart(id: UUID, partId: UUID?, lecturerId: UUID, load: BigDecimal) {
        val loadUnitDistributedPart = distributedLoadRepository.save(DbLoadUnitDistributedPart.fromTemplate(id, partId, lecturerId, load))
        distributedLoadRepository.refresh(loadUnitDistributedPart)
    }

}