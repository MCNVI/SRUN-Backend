package ru.mirea.ippo.backend.database.entities

import ru.mirea.ippo.backend.models.LoadUnit
import java.math.BigDecimal
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "load", schema = "ippo")
data class DbLoadUnit (
    @Id
    val id: UUID,
    val subject: String,
    @ManyToOne
    @JoinColumn(name="groupCode",referencedColumnName = "code",insertable = false, updatable = false)
    val group: DbGroup?,
    @ManyToOne
    @JoinColumn(name="streamId",referencedColumnName = "id",insertable = false, updatable = false)
    val stream: DbStream?,
    val hours: BigDecimal,
    val hoursType: String,
    @OneToMany(mappedBy = "loadUnit")
    val distributedParts: List<DbLoadUnitDistributedPart>
) {
    fun toModel(): LoadUnit = LoadUnit(
        id,
        subject,
        group?.toModel(),
        stream?.toModel(),
        hours,
        hoursType,
        distributedParts.map { it.toModel() }
    )
}