package ru.mirea.ippo.backend.controllers

import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.mirea.ippo.backend.models.*
import ru.mirea.ippo.backend.services.CurriculumService
import java.io.File
import java.util.*
import javax.annotation.security.RolesAllowed
import kotlin.random.Random


@RestController
@RequestMapping("curriculums")
@CrossOrigin
class CurriculumController(val curriculumService: CurriculumService) {

    @GetMapping
    @Secured("ROLE_ADMIN")
    fun getCurriculums(): List<CurriculumShort> {
        return curriculumService.findAll()
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("{id}")
    fun getCurriculum(@PathVariable id: UUID): Curriculum =
        curriculumService.find(id)

    @Secured("ROLE_ADMIN")
    @PostMapping
    fun createCurriculumFromFile(@RequestParam("file") file: MultipartFile) {
        val curriculum: File = File.createTempFile(Random(23).toString(), "tmp")
        curriculum.deleteOnExit()
        file.transferTo(curriculum)
        curriculumService.createFromFile(curriculum)
        curriculum.delete()
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("{id}")
    fun deleteCurriculum(@PathVariable id: UUID) =
        curriculumService.delete(id)

    @Secured("ROLE_ADMIN")
    @PostMapping("{curriculumId}")
    fun createOrUpdateCurriculumUnit(@PathVariable curriculumId: UUID, @RequestBody curriculumUnit: CurriculumUnitTemplate): CurriculumUnit {
        return curriculumService.createOrUpdateUnit(curriculumId, curriculumUnit)
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("{curriculumId}/{unitId}")
    fun deleteCurriculumUnit(@PathVariable curriculumId: UUID, @PathVariable unitId: UUID) {
        return curriculumService.deleteCurriculumUnit(curriculumId, unitId)
    }



}