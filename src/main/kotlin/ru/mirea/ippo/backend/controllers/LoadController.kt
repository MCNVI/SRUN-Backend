package ru.mirea.ippo.backend.controllers

import org.springframework.web.bind.annotation.*
import ru.mirea.ippo.backend.models.LoadUnit
import ru.mirea.ippo.backend.models.LoadUnitDistributedPart
import ru.mirea.ippo.backend.services.LoadService
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("load")
@CrossOrigin
class LoadController(val loadService: LoadService) {

    @GetMapping
    fun getAll(): List<LoadUnit> = loadService.findAll()

    @GetMapping("{id}/parts")
    fun getLoadUnitDistributedParts(@PathVariable id: UUID): List<LoadUnitDistributedPart> = loadService.findDistributedParts(id)

    @GetMapping("{id}")
    fun getLoadUnitById(@PathVariable id: UUID): LoadUnit = loadService.findById(id)

    @DeleteMapping("{id}")
    fun deleteLoadUnit(@PathVariable id: UUID) = loadService.deleteLoadUnit(id)

    @DeleteMapping("{id}/{partId}")
    fun deleteLoadUnitDistributedPart(@PathVariable id: UUID, @PathVariable partId: UUID) = loadService.deleteLoadUnitDistributedPart(id, partId)



    @PutMapping("{id}")
    fun addOrUpdateLoadUnitDistributedPart(@PathVariable id: UUID, @RequestParam(required = false) partId: UUID?, @RequestParam lecturerId: UUID, @RequestParam load: BigDecimal) =
        loadService.addOrUpdatePart(id, partId, lecturerId, load)

}