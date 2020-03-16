package ru.mirea.ippo.backend.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.mirea.ippo.backend.entities.DbLecturerType
import ru.mirea.ippo.backend.services.DirectoryService

@RestController
@RequestMapping("/directory")
class DirectoryController(val directoryService: DirectoryService) {

    @GetMapping("/lecturerTypes")
    fun getLecturerTypes(): List<DbLecturerType>{
        return directoryService.getLecturerTypes()
    }

}