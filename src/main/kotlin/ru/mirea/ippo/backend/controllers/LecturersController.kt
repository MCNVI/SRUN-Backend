package ru.mirea.ippo.backend.controllers

import com.sun.xml.bind.v2.TODO
import org.springframework.web.bind.annotation.*
import ru.mirea.ippo.backend.models.Lecturer
import ru.mirea.ippo.backend.models.LecturerTemplate
import ru.mirea.ippo.backend.services.LecturerService
import java.util.*

@RestController
@RequestMapping("lecturers")
class LecturersController(val lecturerService: LecturerService) {

    @PostMapping
    fun createLecturer(@RequestBody lecturerTemplate: LecturerTemplate): Lecturer = lecturerService.create(lecturerTemplate)

    @GetMapping
    fun getLecturers(): List<Lecturer> = lecturerService.findAll()

    @GetMapping("{id}")
    fun getLecturerById(@PathVariable id: UUID): Lecturer = lecturerService.find(id)

    @PutMapping("{id}")
    fun updateLecturer(@PathVariable id: UUID,@RequestBody lecturerTemplate: LecturerTemplate): Lecturer = lecturerService.update(id,lecturerTemplate)

    @DeleteMapping("{id}")
    fun deleteLecturer(@PathVariable id: UUID) = lecturerService.delete(id)

}