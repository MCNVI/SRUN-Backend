package ru.mirea.ippo.backend.controllers

import org.springframework.web.bind.annotation.*
import ru.mirea.ippo.backend.models.LecturerType
import ru.mirea.ippo.backend.models.LecturerTypeTemplate
import ru.mirea.ippo.backend.services.DirectoryService
import java.util.*

@RestController
@RequestMapping("directory")
@CrossOrigin
class DirectoryController(val directoryService: DirectoryService) {

    @GetMapping("lecturerTypes")
    fun getLecturerTypes(): List<LecturerType> {
        return directoryService.getLecturerTypes()
    }

    @PostMapping("lecturerTypes")
    fun updateLecturerTypes(@RequestBody types: List<LecturerTypeTemplate>): List<LecturerType> {
        return directoryService.updateLecturerTypes(types)
    }

    @DeleteMapping("lecturerTypes/{id}")
    fun deleteLecturerType(@PathVariable id: UUID) {
        directoryService.deleteLecturerType(id)
    }


}