package ru.mirea.ippo.backend.database.entities

import ru.mirea.ippo.backend.models.CurriculumShort
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "curriculum", schema = "ippo")
data class DbCurriculumShort(
    @Id
    val id: UUID,
    val fieldOfStudy: String,
    val educationalProfile: String,
    val startYear: Int
) {
    fun toModel(): CurriculumShort = CurriculumShort(
        id,
        fieldOfStudy,
        educationalProfile,
        startYear
    )
    companion object {
        fun fromTemplate(curriculumShortTemplate: CurriculumShort): DbCurriculumShort = DbCurriculumShort(
            curriculumShortTemplate.id ?: UUID.randomUUID(),
            curriculumShortTemplate.fieldOfStudy,
            curriculumShortTemplate.profile,
            curriculumShortTemplate.startYear
        )
    }

}