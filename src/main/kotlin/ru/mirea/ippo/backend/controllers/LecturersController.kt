package ru.mirea.ippo.backend.controllers

import org.springframework.core.io.InputStreamResource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.mirea.ippo.backend.models.Lecturer
import ru.mirea.ippo.backend.models.LecturerTemplate
import ru.mirea.ippo.backend.models.PrioritizedLecturer
import ru.mirea.ippo.backend.services.LecturerService
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.util.*


@RestController
@RequestMapping("lecturers")
@CrossOrigin
class LecturersController(val lecturerService: LecturerService) {

    @PostMapping
    fun createOrUpdateLecturer(@RequestBody lecturerTemplate: LecturerTemplate): Lecturer {
        val lecturer = lecturerService.createOrUpdate(lecturerTemplate)
        return lecturerService.find(lecturer.id)
    }

    @GetMapping
    fun getLecturers(): List<Lecturer> = lecturerService.findAll()

    @GetMapping("relevance")
    fun getLecturersByRelevance(@RequestParam loadUnitId: UUID): List<PrioritizedLecturer> {
        lecturerService.getByRelevance(loadUnitId)
        return emptyList()
    }

    @GetMapping("{id}")
    fun getLecturerById(@PathVariable id: UUID): Lecturer = lecturerService.find(id)

    @DeleteMapping("{id}")
    fun deleteLecturer(@PathVariable id: UUID) = lecturerService.delete(id)

    @GetMapping("{id}/individualPlan")
    fun getLecturerIndividualPlan(@PathVariable id: UUID): ResponseEntity<InputStreamResource> {
        val individualPlan = lecturerService.createIndividualPlan(id)
        val isr = InputStreamResource(FileInputStream(individualPlan))
        val headers = HttpHeaders()
        headers.setContentDisposition(ContentDisposition.builder("attachment")
            .filename(individualPlan.name, StandardCharsets.UTF_8)
            .build())
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(isr);
    }

}