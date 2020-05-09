package ru.mirea.ippo.backend.database.entities

import ru.mirea.ippo.backend.models.LecturerAcademicRank
import ru.mirea.ippo.backend.models.LecturerDegree
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(schema = "ippo", name = "lecturer_academic_rank")
data class DbLecturerAcademicRank(
    @Id
    val academicRank: String
) {
    fun toModel(): LecturerAcademicRank = LecturerAcademicRank(academicRank)
}