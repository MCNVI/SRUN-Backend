package ru.mirea.ippo.backend.controllers

import org.springframework.core.io.InputStreamResource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.mirea.ippo.backend.models.LoadUnit
import ru.mirea.ippo.backend.models.LoadUnitDistributedPart
import ru.mirea.ippo.backend.services.LoadService
import java.io.FileInputStream
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
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

    @GetMapping("form13")
    fun getForm13(): ResponseEntity<InputStreamResource> {
        val form13 = loadService.createForm13()//lecturerService.createIndividualPlan(id)
        val isr = InputStreamResource(FileInputStream(form13))
        val headers = HttpHeaders()
        headers.setContentDisposition(
            ContentDisposition.builder("attachment")
            .filename(form13.name, StandardCharsets.UTF_8)
            .build())
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(isr);
    }

    @PutMapping("{id}")
    fun addOrUpdateLoadUnitDistributedPart(@PathVariable id: UUID, @RequestParam(required = false) partId: UUID?, @RequestParam lecturerId: UUID, @RequestParam load: BigDecimal) =
        loadService.addOrUpdatePart(id, partId, lecturerId, load)

}