package ru.mirea.ippo.backend.controllers

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import ru.mirea.ippo.backend.models.Group
import ru.mirea.ippo.backend.services.StudentService

@RestController
@Secured("ROLE_ADMIN")
@RequestMapping("groups")
@CrossOrigin
class GroupsController(val studentService: StudentService) {

    @GetMapping("{code}")
    fun getGroupById(@PathVariable code: String): Group = studentService.findGroup(code)

    @GetMapping
    fun getGroups(): List<Group> = studentService.findAllGroups()

    @PostMapping
    fun createGroup(@RequestBody groupTemplate: Group): Group = studentService.createOrUpdateGroup(groupTemplate)

    @DeleteMapping("{code}")
    fun deleteGroup(@PathVariable code: String) = studentService.deleteGroup(code)

}