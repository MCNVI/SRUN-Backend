package ru.mirea.ippo.backend.database.entities

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(schema = "ippo", name = "group_stream")
data class DbGroupStream (
    @Id
    val streamId: UUID,
    val groupCode: String
) {
    companion object{
        fun fromGroup(streamId: UUID, groupCode: String) : DbGroupStream = DbGroupStream(
            streamId,
            groupCode
        )
    }
}