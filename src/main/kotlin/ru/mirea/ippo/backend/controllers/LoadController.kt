package ru.mirea.ippo.backend.controllers

import org.springframework.core.io.InputStreamResource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import ru.mirea.ippo.backend.models.LoadUnit
import ru.mirea.ippo.backend.models.LoadUnitDistributedPart
import ru.mirea.ippo.backend.repositories.UserRepository
import ru.mirea.ippo.backend.services.LoadService
import java.io.FileInputStream
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.util.*

@RestController
@Secured("ROLE_DEPARTMENT_HEAD")
@RequestMapping("load")
@CrossOrigin
class LoadController(val loadService: LoadService, val userRepository: UserRepository) {

    @GetMapping
    fun getAll(@AuthenticationPrincipal username: String): List<LoadUnit> {
        val user = userRepository.findByUsername(username) ?: throw Exception()
        return loadService.findAll(user.department)
    }

    @GetMapping("{id}")
    fun getLoadUnitById(@PathVariable id: UUID): LoadUnit = loadService.findById(id)

    @DeleteMapping("{id}")
    fun deleteLoadUnit(@PathVariable id: UUID) = loadService.deleteLoadUnit(id)

    @GetMapping("{id}/parts")
    fun getLoadUnitDistributedParts(@PathVariable id: UUID): List<LoadUnitDistributedPart> {
        return loadService.findDistributedParts(id)
    }


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