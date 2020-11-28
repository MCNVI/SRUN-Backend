package ru.mirea.ippo.backend.controllers

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import ru.mirea.ippo.backend.models.Group
import ru.mirea.ippo.backend.models.Stream
import ru.mirea.ippo.backend.services.StudentService
import java.util.*

@RestController
@Secured("ROLE_ADMIN")
@RequestMapping("streams")
@CrossOrigin
class StreamsController(val studentService: StudentService) {

    @GetMapping("{id}")
    fun getStreamById(@PathVariable id: UUID): Stream = studentService.findStream(id)

    @GetMapping
    fun getStreams(): List<Stream> = studentService.findAllStreams()

    @PostMapping
    fun createStream(@RequestBody streamTemplate: Stream): Stream = studentService.createStream(streamTemplate)

    @DeleteMapping("{id}")
    fun deleteStream(@PathVariable id: UUID) = studentService.deleteStream(id)

    @PutMapping("{id}/addGroup")
    fun addGroupToStream(@PathVariable id: UUID, @RequestBody group: Group) : Stream = studentService.addGroupToStream(id, group)

    @PutMapping("{id}/removeGroup")
    fun removeGroupFromStream(@PathVariable id: UUID, @RequestBody group: Group) = studentService.removeGroupFromStream(id, group)

}