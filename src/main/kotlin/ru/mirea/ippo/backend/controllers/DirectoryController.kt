package ru.mirea.ippo.backend.controllers

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import ru.mirea.ippo.backend.models.LecturerAcademicRank
import ru.mirea.ippo.backend.models.LecturerDegree
import ru.mirea.ippo.backend.models.LecturerType
import ru.mirea.ippo.backend.models.LecturerTypeTemplate
import ru.mirea.ippo.backend.services.DirectoryService
import java.util.*

@RestController
@RequestMapping("directory")
@CrossOrigin
class DirectoryController(val directoryService: DirectoryService) {

    @Secured("ROLE_DEPARTMENT_HEAD","ROLE_ADMIN")
    @GetMapping("lecturerTypes")
    fun getLecturerTypes(): List<LecturerType> {
        return directoryService.getLecturerTypes()
    }

    @Secured("ROLE_DEPARTMENT_HEAD","ROLE_ADMIN")
    @GetMapping("degrees")
    fun getLecturerDegrees(): List<LecturerDegree> {
        return directoryService.getLecturerDegrees()
    }

    @Secured("ROLE_DEPARTMENT_HEAD","ROLE_ADMIN")
    @GetMapping("academicRanks")
    fun getLecturerAcademicRanks(): List<LecturerAcademicRank> {
        return directoryService.getLecturerAcademicRanks()
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("lecturerTypes")
    fun updateLecturerTypes(@RequestBody types: List<LecturerTypeTemplate>): List<LecturerType> {
        return directoryService.updateLecturerTypes(types)
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("lecturerTypes/{id}")
    fun deleteLecturerType(@PathVariable id: UUID) {
        directoryService.deleteLecturerType(id)
    }


}