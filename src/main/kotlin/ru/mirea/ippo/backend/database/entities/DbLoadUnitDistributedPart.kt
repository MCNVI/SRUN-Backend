package ru.mirea.ippo.backend.database.entities

import ru.mirea.ippo.backend.models.LoadUnitDistributedPart
import java.math.BigDecimal
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "load_distribution", schema = "ippo")
data class DbLoadUnitDistributedPart (
    @Id
    val id: UUID,
    val loadUnitId: UUID,
    val lecturerId: UUID,
    @ManyToOne
    @JoinColumn(name="loadUnitId",referencedColumnName = "id",insertable = false, updatable = false)
    val loadUnit: DbLoadUnit?,
    @ManyToOne
    @JoinColumn(name="lecturerId",referencedColumnName = "id",insertable = false, updatable = false)
    val lecturer: DbLecturer?,
    val loadPart: BigDecimal
) {
    fun toModel(): LoadUnitDistributedPart = LoadUnitDistributedPart(
        id,
        loadUnitId,
        lecturer?.toModel(),
        loadPart
    )
    companion object {
        fun fromTemplate(id: UUID, partId: UUID?, lecturerId: UUID, loadPart: BigDecimal): DbLoadUnitDistributedPart = DbLoadUnitDistributedPart(
            partId ?: UUID.randomUUID(),
            id,
            lecturerId,
            null,
            null,
            loadPart
        )
    }
}