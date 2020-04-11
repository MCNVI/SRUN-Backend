package ru.mirea.ippo.backend.database.entities

import ru.mirea.ippo.backend.models.Stream
import java.util.*
import javax.persistence.*

@Entity
@Table(schema = "ippo", name = "stream")
data class DbStream (
    @Id
    val id: UUID,
    val type: String,
    val subject: String,
    val course: Int,
    @ManyToMany
    @JoinTable(name = "group_stream",
        joinColumns = [JoinColumn(name = "stream_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "group_code", referencedColumnName = "code")]
    )
    val groups: List<DbGroup>,
    val curriculumId: UUID
) {
    fun toModel() : Stream = Stream(
        id,
        type,
        subject,
        course,
        groups.map { it.toModel() },
        curriculumId
    )
    companion object {
        fun fromTemplate(streamTemplate: Stream): DbStream = DbStream(
            streamTemplate.id ?: UUID.randomUUID(),
            streamTemplate.type,
            streamTemplate.subject,
            streamTemplate.course,
            streamTemplate.groups?.map {  DbGroup.fromTemplate(it) } ?: emptyList(),
            streamTemplate.curriculumId
        )
    }
}