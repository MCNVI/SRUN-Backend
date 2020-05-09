package ru.mirea.ippo.backend.controllers

import org.springframework.core.io.InputStreamResource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import ru.mirea.ippo.backend.models.Lecturer
import ru.mirea.ippo.backend.models.LecturerTemplate
import ru.mirea.ippo.backend.models.PrioritizedLecturer
import ru.mirea.ippo.backend.repositories.UserRepository
import ru.mirea.ippo.backend.services.LecturerService
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.util.*


@RestController

@RequestMapping("lecturers")
@CrossOrigin
class LecturersController(val lecturerService: LecturerService, val userRepository: UserRepository) {

    @Secured("ROLE_DEPARTMENT_HEAD")
    @PostMapping
    fun createOrUpdateLecturer(@RequestBody lecturerTemplate: LecturerTemplate, @AuthenticationPrincipal username: String): Lecturer {
        val user = userRepository.findByUsername(username) ?: throw Exception()
        val lecturer = lecturerService.createOrUpdate(lecturerTemplate, user.department)
        return lecturerService.find(lecturer.id)
    }

    @Secured("ROLE_DEPARTMENT_HEAD")
    @GetMapping
    fun getLecturers(@AuthenticationPrincipal username: String): List<Lecturer> {
        val user = userRepository.findByUsername(username) ?: throw Exception()
        return lecturerService.findAllByDepartment(user.department)
    }

    //TODO: findByRelevance
    @Secured("ROLE_DEPARTMENT_HEAD")
    @GetMapping("relevance")
    fun getLecturersByRelevance(@RequestParam loadUnitId: UUID, @AuthenticationPrincipal username: String): List<PrioritizedLecturer> {
        lecturerService.getByRelevance(loadUnitId)
        return emptyList()
    }

    @Secured("ROLE_DEPARTMENT_HEAD")
    @GetMapping("{id}")
    fun getLecturerById(@PathVariable id: UUID): Lecturer = lecturerService.find(id)

    @Secured("ROLE_DEPARTMENT_HEAD")
    @DeleteMapping("{id}")
    fun deleteLecturer(@PathVariable id: UUID) = lecturerService.delete(id)

    @Secured("ROLE_DEPARTMENT_HEAD")
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