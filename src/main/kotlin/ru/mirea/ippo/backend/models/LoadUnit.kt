package ru.mirea.ippo.backend.models

import java.math.BigDecimal
import java.util.*

data class LoadUnit (
    val id: UUID,
    val subject: String,
    val group: Group?,
    val stream: Stream?,
    val hours: BigDecimal,
    val hoursType: String,
    val loadUnitPartDistributedParts: List<LoadUnitDistributedPart>
)