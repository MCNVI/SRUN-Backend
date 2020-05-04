package ru.mirea.ippo.backend.models

import ru.mirea.ippo.backend.database.entities.DbLoadUnit
import java.math.BigDecimal
import java.util.*

data class LoadUnitDistributedPart (
    val id: UUID,
    val loadUnitId: UUID,
    val lecturer: Lecturer?,
    val loadPart: BigDecimal
)
