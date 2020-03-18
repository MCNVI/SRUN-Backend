package ru.mirea.ippo.backend.database.entities

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
    override val id: UUID,
    val type: String,
    val hours: Int,
    val isPartTime: Boolean,
    val isExternal: Boolean
) : Identified<UUID> {
    fun toModel(): LecturerType = LecturerType(
        id,
        type,
        hours,
        isPartTime,
        isExternal
    )
}

@Entity
@Table(name = "lecturer_type", schema = "ippo")
data class DbInsertableLecturerType (
    @Id
    override val id: UUID?,
    val type: String,
    val hours: Int,
    val isPartTime: Boolean,
    val isExternal: Boolean
) : Insertable<DbInsertableLecturerType,UUID> {
    companion object{
        fun fromTemplate(type: LecturerTypeTemplate) = DbInsertableLecturerType(
            type.id,
            type.type,
            type.hours,
            type.isPartTime,
            type.isExternal ?: false
        )
    }

    override fun clone(): DbInsertableLecturerType {
        return this.copy()
    }
}