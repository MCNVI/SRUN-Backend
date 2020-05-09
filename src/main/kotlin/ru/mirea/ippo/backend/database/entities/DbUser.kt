package ru.mirea.ippo.backend.database.entities

import com.vladmihalcea.hibernate.type.array.StringArrayType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import ru.mirea.ippo.backend.models.CustomUser
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(schema = "ippo", name = "user")
@TypeDef(
    name = "string-array",
    typeClass = StringArrayType::class
)
data class DbUser(
    @Id
    val id: UUID,
    val username: String,
    val password: String,
    @Type(type = "string-array")
    val roles: Array<String>,
    val department: Int
) {
    fun toModel(): CustomUser = CustomUser(
        id, username, password, roles
    )
}