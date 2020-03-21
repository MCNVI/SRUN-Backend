package ru.mirea.ippo.backend.database.entities

import org.hibernate.annotations.GenericGenerator
import ru.mirea.ippo.backend.models.Lecturer
import ru.mirea.ippo.backend.models.LecturerTemplate
import java.math.BigDecimal
import java.util.*
import javax.persistence.*

@Entity
@Table(schema = "ippo", name = "staffing_table")
data class DbLecturer (
    @Id
    override val id: UUID,
    @ManyToOne
    val lecturerType: DbLecturerType,
    val name: String,
    val middleName: String,
    val lastName: String,
    val lecturerRate: BigDecimal,
    val lecturerHoursForRate: BigDecimal,
    val lecturerMaxHoursForRate: BigDecimal
) : Identified<UUID> {
    fun toModel(): Lecturer = Lecturer(
        id,
        lecturerType.toModel(),
        name,
        middleName,
        lastName,
        lecturerRate,
        lecturerHoursForRate,
        lecturerMaxHoursForRate
    )
}

@Entity
@Table(schema = "ippo", name = "staffing_table")
data class DbInsertableLecturer (
    @Id
    override val id: UUID?,
    @Column(name = "lecturer_type_id")
    val lecturerTypeId: UUID,
    val name: String,
    val middleName: String,
    val lastName: String,
    val lecturerRate: BigDecimal,
    val lecturerHoursForRate: BigDecimal,
    val lecturerMaxHoursForRate: BigDecimal
) : Insertable<DbInsertableLecturer,UUID> {
    companion object{
        fun fromTemplate(lecturer: LecturerTemplate) = DbInsertableLecturer(
            lecturer.id,
            lecturer.lecturerTypeId,
            lecturer.name,
            lecturer.middleName,
            lecturer.lastName,
            lecturer.lecturerRate,
            lecturer.lecturerHoursForRate,
            lecturer.lecturerMaxHoursForRate
        )
    }

    override fun clone(): DbInsertableLecturer {
        return this.copy()
    }
}