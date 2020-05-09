package ru.mirea.ippo.backend.database.entities

import ru.mirea.ippo.backend.models.LecturerDegree
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(schema = "ippo", name = "lecturer_degree")
data class DbLecturerDegree(
    @Id
    val degree: String
) {
    fun toModel(): LecturerDegree = LecturerDegree(degree)
}