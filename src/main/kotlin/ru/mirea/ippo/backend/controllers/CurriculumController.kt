package ru.mirea.ippo.backend.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.mirea.ippo.backend.models.Curriculum
import ru.mirea.ippo.backend.models.CurriculumShort
import ru.mirea.ippo.backend.services.CurriculumService
import java.io.File
import java.util.*
import kotlin.random.Random


@RestController
@RequestMapping("curriculums")
@CrossOrigin
class CurriculumController(val curriculumService: CurriculumService) {

    @GetMapping
    fun getCurriculums(): List<CurriculumShort> = curriculumService.findAll()

    @GetMapping("{id}")
    fun getCurriculum(@PathVariable id: UUID): Curriculum =
        curriculumService.find(id)

    @PostMapping
    fun createCurriculumFromFile(@RequestParam("file") file: MultipartFile) {
        val curriculum: File = File.createTempFile(Random(23).toString(), "tmp")
        curriculum.deleteOnExit()
        file.transferTo(curriculum)
        curriculumService.createFromFile(curriculum)
        curriculum.delete()
    }




}