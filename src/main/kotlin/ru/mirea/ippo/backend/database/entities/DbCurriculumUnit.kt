package ru.mirea.ippo.backend.database.entities

import ru.mirea.ippo.backend.models.CurriculumUnit
import java.math.BigDecimal
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "curriculum_unit", schema = "ippo")
data class DbCurriculumUnit(
    @Id
    val id: UUID,
    val course: Short,
    val semester: Short,
    val subject: String,
    val loadType: String,
    val load: BigDecimal?,
    val curriculumId: UUID
) {
    fun toModel(): CurriculumUnit = CurriculumUnit(
        id,
        course,
        semester,
        subject,
        loadType,
        load,
        curriculumId
    )
    companion object{
        fun fromTemplate(curriculumUnit: CurriculumUnit) = DbCurriculumUnit(
            curriculumUnit.id ?: UUID.randomUUID(),
            curriculumUnit.course,
            curriculumUnit.semester,
            curriculumUnit.subject,
            curriculumUnit.loadType,
            curriculumUnit.load,
            curriculumUnit.curriculumId
        )
    }
}