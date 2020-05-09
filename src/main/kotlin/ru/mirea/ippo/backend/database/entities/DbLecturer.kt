package ru.mirea.ippo.backend.database.entities

import ru.mirea.ippo.backend.models.Lecturer
import ru.mirea.ippo.backend.models.LecturerAcademicRank
import ru.mirea.ippo.backend.models.LecturerDegree
import ru.mirea.ippo.backend.models.LecturerTemplate
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(schema = "ippo", name = "staffing_table")
data class DbLecturer (
    @Id
    val id: UUID,
    val lecturerTypeId: UUID,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="lecturerTypeId",referencedColumnName = "id",insertable = false, updatable = false)
    val lecturerType: DbLecturerType?,
    val academicRank: String?,
    val degree: String?,
    val name: String,
    val middleName: String,
    val lastName: String,
    val lecturerRate: BigDecimal,
    val lecturerLoadForRate: BigDecimal,
    val lecturerMaxLoadForRate: BigDecimal,
    val employmentStartDate: Instant,
    val employmentFinishDate: Instant,
    val monthAmount: BigDecimal,
    val department: Int
) {
    fun toModel(): Lecturer = Lecturer(
        id,
        lecturerType?.toModel(),
        LecturerAcademicRank(academicRank),
        LecturerDegree(degree),
        name,
        middleName,
        lastName,
        lecturerRate,
        lecturerLoadForRate,
        lecturerMaxLoadForRate,
        employmentStartDate,
        employmentFinishDate,
        monthAmount
    )
    companion object{
        fun fromTemplate(lecturer: LecturerTemplate, lecturerLoadForRate: BigDecimal, lecturerMaxLoadForRate: BigDecimal, department: Int) = DbLecturer(
            lecturer.id ?: UUID.randomUUID(),
            lecturer.lecturerTypeId,
            null,
            lecturer.academicRank?.academicRank,
            lecturer.degree?.degree,
            lecturer.name,
            lecturer.middleName,
            lecturer.lastName,
            lecturer.lecturerRate,
            lecturerLoadForRate,
            lecturerMaxLoadForRate,
            lecturer.employmentStartDate,
            lecturer.employmentFinishDate,
            lecturer.monthAmount,
            department
        )
    }
}