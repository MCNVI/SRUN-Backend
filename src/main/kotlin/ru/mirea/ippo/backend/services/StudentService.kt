package ru.mirea.ippo.backend.services

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.mirea.ippo.backend.database.entities.DbGroup
import ru.mirea.ippo.backend.database.entities.DbGroupStream
import ru.mirea.ippo.backend.database.entities.DbStream
import ru.mirea.ippo.backend.errorhandling.ObjectNotFoundException
import ru.mirea.ippo.backend.models.Group
import ru.mirea.ippo.backend.models.Stream
import ru.mirea.ippo.backend.repositories.GroupRepository
import ru.mirea.ippo.backend.repositories.GroupStreamRepository
import ru.mirea.ippo.backend.repositories.StreamRepository
import java.util.*

@Service
class StudentService(
    val groupRepository: GroupRepository,
    val streamRepository: StreamRepository,
    val groupStreamRepository: GroupStreamRepository
) {

    fun findGroup(code: String): Group =
        groupRepository.findByIdOrNull(code)?.toModel() ?: throw ObjectNotFoundException("Group", code)

    fun findAllGroups(): List<Group> = groupRepository.findAll().map { it.toModel() }

    fun createOrUpdateGroup(group: Group): Group {
        val dbGroup = DbGroup.fromTemplate(group)
        val group = groupRepository.save(dbGroup)
        groupRepository.refresh(group)
        return groupRepository.findByIdOrNull(group.code)?.toModel() ?: throw ObjectNotFoundException(
            "Group",
            group.code
        )
    }

    fun deleteGroup(code: String) = groupRepository.deleteById(code)

    fun findStream(id: UUID): Stream =
        streamRepository.findByIdOrNull(id)?.toModel() ?: throw ObjectNotFoundException("Stream", id)

    fun findAllStreams(): List<Stream> = streamRepository.findAll().map { it.toModel() }

    fun createStream(streamTemplate: Stream): Stream {
        val dbStream = DbStream.fromTemplate(streamTemplate)
        val stream = streamRepository.save(dbStream)
        streamRepository.refresh(stream)
        return streamRepository.findByIdOrNull(stream.id)?.toModel() ?: throw ObjectNotFoundException(
            "Stream",
            stream.id
        )
    }

    fun deleteStream(id: UUID) = streamRepository.deleteById(id)

    fun addGroupToStream(streamId: UUID, group: Group): Stream {
        val dbGroupStream = DbGroupStream.fromGroup(streamId, group.code)
        val groupStream = groupStreamRepository.save(dbGroupStream)
        groupStreamRepository.refresh(groupStream)
        return streamRepository.findByIdOrNull(streamId)?.toModel() ?: throw ObjectNotFoundException("Stream", streamId)
    }

    @Transactional
    fun removeGroupFromStream(streamId: UUID, group: Group) {
        groupStreamRepository.deleteByStreamIdAndGroupCode(streamId, group.code)
    }

    fun getAvailableGroups(streamId: UUID): List<Group> {
        val requestedStream = streamRepository.findByIdOrNull(streamId) ?: throw ObjectNotFoundException("Stream", streamId)
        val bannedGroupCodes = streamRepository.findAllByCurriculumIdAndSubjectAndCourse(
            requestedStream.curriculumId,
            requestedStream.subject,
            requestedStream.course
        ).mapNotNull { it.toModel().groups }.flatten().map { it.code }
        return groupRepository.findAllByCodeNotIn(bannedGroupCodes).map { it.toModel() }
    }


}