package ru.mirea.ippo.backend.database.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import ru.mirea.ippo.backend.models.Group
import java.util.*
import javax.persistence.*


@Entity
@Table(schema = "ippo", name = "group")
data class DbGroup (
    @Id
    val code: String,
    val studentsNumber: Int,
    val course: Int,
    @ManyToOne
    @JoinColumn(name="curriculumId",referencedColumnName = "id",insertable = false, updatable = false)
    val curriculum: DbCurriculumShort?,
    val curriculumId: UUID
) {
    fun toModel() : Group = Group(
        code,
        studentsNumber,
        course,
        curriculum?.toModel(),
        curriculumId
    )
    companion object {
        fun fromTemplate(groupTemplate: Group): DbGroup = DbGroup(
            groupTemplate.code,
            groupTemplate.studentsNumber,
            groupTemplate.course,
            null,
            groupTemplate.curriculumId
        )
    }
}