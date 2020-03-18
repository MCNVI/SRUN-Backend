package ru.mirea.ippo.backend.database

import io.ebean.ExpressionList
import io.ebean.Query
import ru.mirea.ippo.backend.models.*
import ru.mirea.ippo.backend.errorhandling.FilterException
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KProperty1

data class ValueMap(val type: FilterValueType, val path: String)

inline fun <reified T, reified P1, reified P2> propertyPath(k1: KProperty1<T, P1?>, k2: KProperty1<P1, P2>): String {
    return k1.name + "." + k2.name
}

fun throwNullFilterValue(objName: String?, propName: String): Nothing {
    throw FilterException(objName, "unexpected null filter value on path $propName")
}

fun throwUnexpectedFilterCondition(objName: String?, cond: FilterConditionType, type: FilterValueType, propName: String): Nothing {
    throw FilterException(objName, "unexpected filter condition $cond for type $type on path $propName")
}

inline fun <reified T> applyStringFilter(where: ExpressionList<T>, valueMap: ValueMap, value: FilterValue): ExpressionList<T> {
    return when (value.conditionType) {
        FilterConditionType.CONTAINS ->
            where.icontains(valueMap.path, value.string ?: throwNullFilterValue(T::class.simpleName, valueMap.path))
        FilterConditionType.STARTS_WITH ->
            where.istartsWith(valueMap.path, value.string ?: throwNullFilterValue(T::class.simpleName, valueMap.path))
        FilterConditionType.EQUAL ->
            where.ieq(valueMap.path, value.string ?: throwNullFilterValue(T::class.simpleName, valueMap.path))
        FilterConditionType.NOT_EQUAL ->
            where.ine(valueMap.path, value.string ?: throwNullFilterValue(T::class.simpleName, valueMap.path))
        else -> throwUnexpectedFilterCondition(T::class.simpleName, value.conditionType, valueMap.type, valueMap.path)
    }
}

inline fun <reified T> applyNumericFilter(where: ExpressionList<T>, valueMap: ValueMap, cond: FilterConditionType, value: Any?): ExpressionList<T> {
    return when (cond) {
        FilterConditionType.EQUAL ->
            where.eq(valueMap.path, value ?: throwNullFilterValue(T::class.simpleName, valueMap.path))
        FilterConditionType.NOT_EQUAL ->
            where.ne(valueMap.path, value ?: throwNullFilterValue(T::class.simpleName, valueMap.path))
        FilterConditionType.GREATER_THAN ->
            where.gt(valueMap.path, value ?: throwNullFilterValue(T::class.simpleName, valueMap.path))
        FilterConditionType.GREATER_THAN_OR_EQUAL ->
            where.ge(valueMap.path, value ?: throwNullFilterValue(T::class.simpleName, valueMap.path))
        FilterConditionType.LESS_THAN ->
            where.lt(valueMap.path, value ?: throwNullFilterValue(T::class.simpleName, valueMap.path))
        FilterConditionType.LESS_THAN_OR_EQUAL ->
            where.le(valueMap.path, value ?: throwNullFilterValue(T::class.simpleName, valueMap.path))
        else -> throwUnexpectedFilterCondition(T::class.simpleName, cond, valueMap.type, valueMap.path)
    }
}

inline fun <reified T> applyLongintFilter(where: ExpressionList<T>, valueMap: ValueMap, value: FilterValue): ExpressionList<T>
        = applyNumericFilter(where, valueMap, value.conditionType, value.longint)

inline fun <reified T> applyDecimalFilter(where: ExpressionList<T>, valueMap: ValueMap, value: FilterValue): ExpressionList<T>
        = applyNumericFilter(where, valueMap, value.conditionType, value.decimal)

inline fun <reified T> applyDateFilter(where: ExpressionList<T>, valueMap: ValueMap, value: FilterValue): ExpressionList<T>
        = applyNumericFilter(where, valueMap, value.conditionType, value.date)

inline fun <reified T> applyEquatableFilter(where: ExpressionList<T>, valueMap: ValueMap, cond: FilterConditionType, value: Any?): ExpressionList<T> {
    return when (cond) {
        FilterConditionType.EQUAL ->
            where.eq(valueMap.path, value ?: throwNullFilterValue(T::class.simpleName, valueMap.path))
        FilterConditionType.NOT_EQUAL ->
            where.ne(valueMap.path, value ?: throwNullFilterValue(T::class.simpleName, valueMap.path))
        else -> throwUnexpectedFilterCondition(T::class.simpleName, cond, valueMap.type, valueMap.path)
    }
}

inline fun <reified T> applyBooleanFilter(where: ExpressionList<T>, valueMap: ValueMap, value: FilterValue): ExpressionList<T>
        = applyEquatableFilter(where, valueMap, value.conditionType, value.boolean)

inline fun <reified T> applyGuidFilter(where: ExpressionList<T>, valueMap: ValueMap, value: FilterValue): ExpressionList<T>
        = applyEquatableFilter(where, valueMap, value.conditionType, value.guid)

inline fun <reified T> Query<T>.eFindPage(request: PageRequest, valueMaps: Map<String, ValueMap>, defaultSort: String): Page<T> {
    val defaultCount = 20

    val from = request.from ?: 0
    val count = request.count ?: defaultCount

    var query = this

    val sortProperty = if (request.sortBy != null) {
        valueMaps[request.sortBy]?.path ?: throw FilterException(T::class.simpleName, "unknown property ${request.sortBy}")
    } else defaultSort

    query = if (request.sortDescending) {
        query.order().desc(sortProperty)
    } else {
        query.order().asc(sortProperty)
    }

    if (request.filters != null) {
        for (filter in request.filters) {
            val valueMap = valueMaps[filter.key]
                ?: throw FilterException(T::class.simpleName, "unknown property " + filter.key)

            val disjunctive = filter.value.all { it.conditionType == FilterConditionType.EQUAL }
            var expressionList: ExpressionList<T> = if (disjunctive) query.where().or() else query.where().and()

            for (value in filter.value) {
                if (valueMap.type !== value.type)
                // TODO: more info
                    throw FilterException(T::class.simpleName, "value type mismatch for property " + filter.key)

                expressionList = when (value.type) {
                    FilterValueType.STRING ->
                        applyStringFilter(expressionList, valueMap, value)
                    FilterValueType.BOOLEAN ->
                        applyBooleanFilter(expressionList, valueMap, value)
                    FilterValueType.LONGINT ->
                        applyLongintFilter(expressionList, valueMap, value)
                    FilterValueType.DECIMAL ->
                        applyDecimalFilter(expressionList, valueMap, value)
                    FilterValueType.DATE ->
                        applyDateFilter(expressionList, valueMap, value)
                    FilterValueType.GUID ->
                        applyGuidFilter(expressionList, valueMap, value)
                }
            }

            query = if (disjunctive) expressionList.endOr().query() else expressionList.endAnd().query()
        }
    }

    val total = query.findCount()
    val results = query.setMaxRows(count).setFirstRow(from).findList()

    return Page(
        total,
        max(0, min(from, total - 1)),
        min(from + count - 1, total - 1),
        results
    )
}

inline fun <reified T1, reified T2> Page<T1>.map(mapFn: (x: T1) -> T2): Page<T2> {
    return Page(
        this.totalCount,
        this.from,
        this.to,
        this.items.map(mapFn)
    )
}
