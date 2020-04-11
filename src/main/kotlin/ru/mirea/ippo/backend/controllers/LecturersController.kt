package ru.mirea.ippo.backend.controllers

import com.sun.xml.bind.v2.TODO
import org.springframework.web.bind.annotation.*
import ru.mirea.ippo.backend.models.Lecturer
import ru.mirea.ippo.backend.models.LecturerTemplate
import ru.mirea.ippo.backend.services.LecturerService
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

    @GetMapping("{id}")
    fun getLecturerById(@PathVariable id: UUID): Lecturer = lecturerService.find(id)

    @DeleteMapping("{id}")
    fun deleteLecturer(@PathVariable id: UUID) = lecturerService.delete(id)

}