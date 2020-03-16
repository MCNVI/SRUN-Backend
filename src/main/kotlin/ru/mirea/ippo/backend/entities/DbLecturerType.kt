package ru.mirea.ippo.backend.entities

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "lecturer_type", schema = "ippo")
data class DbLecturerType(
    @Id
    val typeName: String,
    val hours: Int
)