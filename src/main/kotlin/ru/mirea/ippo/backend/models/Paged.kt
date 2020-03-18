package ru.mirea.ippo.backend.models

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Page<T>(
    val totalCount: Int,
    val from: Int,
    val to: Int,
    val items: List<T>
)

enum class FilterValueType {
    STRING,
    DECIMAL,
    LONGINT,
    BOOLEAN,
    DATE,
    GUID
}

enum class FilterConditionType {
    EQUAL,
    NOT_EQUAL,
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN_OR_EQUAL,
    STARTS_WITH,
    CONTAINS
}

data class FilterValue(
    val type: FilterValueType,
    val conditionType: FilterConditionType,
    val string: String?,
    val decimal: BigDecimal?,
    val longint: Long?,
    val boolean: Boolean?,
    val date: Instant?,
    val guid: UUID?
)

data class PageRequest(
    val from: Int?,
    val count: Int?,
    val sortBy: String?,
    val sortDescending: Boolean = false,
    val filters: Map<String, List<FilterValue>>?
)