package ru.mirea.ippo.backend.database.entities

import ru.mirea.ippo.backend.models.LecturerType
import ru.mirea.ippo.backend.models.LecturerTypeTemplate
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "lecturer_type", schema = "ippo")
data class DbLecturerType(
    @Id
    val id: UUID,
    val type: String,
    val studyLoad: Int,
    val isPartTime: Boolean,
    val isExternal: Boolean
) {
    fun toModel(): LecturerType = LecturerType(
        id,
        type,
        studyLoad,
        isPartTime,
        isExternal
    )
    companion object{
        fun fromTemplate(type: LecturerTypeTemplate) = DbLecturerType(
            type.id ?: UUID.randomUUID(),
            type.type,
            type.studyLoad,
            type.isPartTime,
            type.isExternal ?: false
        )
    }
}