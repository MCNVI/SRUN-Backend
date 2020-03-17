package ru.mirea.ippo.backend.entities

import org.hibernate.annotations.GenericGenerator
import ru.mirea.ippo.backend.models.LecturerType
import ru.mirea.ippo.backend.models.LecturerTypeTemplate
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "lecturer_type", schema = "ippo")
data class DbLecturerType(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    val id: UUID?,
    val type: String,
    val hours: Int,
    val isPartTime: Boolean,
    val isExternal: Boolean
) {
    fun toModel(): LecturerType = LecturerType(
        id!!,
        type,
        hours,
        isPartTime,
        isExternal
    )
    companion object{
        fun fromTemplate(type: LecturerTypeTemplate) = DbLecturerType(
            type.id,
            type.type,
            type.hours,
            type.isPartTime,
            type.isExternal ?: false
        )
    }
}