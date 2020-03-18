package ru.mirea.ippo.backend.controllers

import org.springframework.web.bind.annotation.*
import ru.mirea.ippo.backend.models.LecturerType
import ru.mirea.ippo.backend.models.LecturerTypeTemplate
import ru.mirea.ippo.backend.services.DirectoryService

@RestController
@RequestMapping("/directory")
class DirectoryController(val directoryService: DirectoryService) {

    @GetMapping("/lecturerTypes")
    fun getLecturerTypes(): List<LecturerType>{
        return directoryService.getLecturerTypes()
    }

    @PostMapping("/lecturerTypes")
    fun updatreLecturerTypes(@RequestBody types: List<LecturerTypeTemplate>): List<LecturerType>{
        return directoryService.updateLecturerTypes(types)
    }

}