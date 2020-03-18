package ru.mirea.ippo.backend.database

import io.ebean.Query
import io.ebean.typequery.TQRootBean
import kotlin.reflect.KProperty1

class Includer<TStart, TCurrent>(val query: Query<TStart>, val prevPath: String?) {
    private var _resultingQuery: Query<TStart> = query

    val resultingQuery: Query<TStart>
        get() = _resultingQuery

    private fun takeN(name: String, withSeparateQuery: Boolean) {
        val path = if (prevPath == null) name else prevPath + "." + name
        _resultingQuery = if (withSeparateQuery) _resultingQuery.fetchQuery(path) else _resultingQuery.fetch(path)
    }

    fun <P> take(name: KProperty1<TCurrent, P?>, withSeparateQuery: Boolean = false) = takeN(name.name, withSeparateQuery)

    fun <P> takeList(name: KProperty1<TCurrent, List<P>>, withSeparateQuery: Boolean = false) = takeN(name.name, withSeparateQuery)

    private fun <P> takeN(name: String, withSeparateQuery: Boolean, thenFn: (includer: Includer<TStart, P>) -> Unit) {
        val newPath = if (prevPath == null) name else prevPath + "." + name
        val query = if (withSeparateQuery) _resultingQuery.fetchQuery(newPath) else _resultingQuery.fetch(newPath)
        val thenIncluder = Includer<TStart, P>(query, newPath)
        thenFn(thenIncluder)
        _resultingQuery = thenIncluder.resultingQuery
    }

    fun <P> take(name: KProperty1<TCurrent, P?>, withSeparateQuery: Boolean = false, thenFn: (includer: Includer<TStart, P>) -> Unit) {
        takeN(name.name, withSeparateQuery, thenFn)
    }

    fun <P> takeList(name: KProperty1<TCurrent, List<P>>, withSeparateQuery: Boolean = false, thenFn: (includer: Includer<TStart, P>) -> Unit) {
        takeN(name.name, withSeparateQuery, thenFn)
    }
}

inline fun <reified T> Query<T>.include(includeFn: (includer: Includer<T, T>) -> Unit): Query<T> {
    val includer = Includer<T, T>(this, null)
    includeFn(includer)
    return includer.resultingQuery
}

class QIncluder<TStart, TQ : TQRootBean<TStart, TQ>, TCurrent>(val query: TQ, val prevPath: String?) {
    private var _resultingQuery: TQ = query

    val resultingQuery: TQ
        get() = _resultingQuery

    private fun takeN(name: String, withSeparateQuery: Boolean) {
        val path = if (prevPath == null) name else prevPath + "." + name
        _resultingQuery = if (withSeparateQuery) _resultingQuery.fetchQuery(path) else _resultingQuery.fetch(path)
    }

    fun <P> take(name: KProperty1<TCurrent, P?>, withSeparateQuery: Boolean = false) = takeN(name.name, withSeparateQuery)

    fun <P> takeList(name: KProperty1<TCurrent, List<P>>, withSeparateQuery: Boolean = false) = takeN(name.name, withSeparateQuery)

    private fun <P> takeN(name: String, withSeparateQuery: Boolean, thenFn: (includer: QIncluder<TStart, TQ, P>) -> Unit) {
        val newPath = if (prevPath == null) name else prevPath + "." + name
        val query = if (withSeparateQuery) _resultingQuery.fetchQuery(newPath) else _resultingQuery.fetch(newPath)
        val thenIncluder = QIncluder<TStart, TQ, P>(query, newPath)
        thenFn(thenIncluder)
        _resultingQuery = thenIncluder.resultingQuery
    }

    fun <P> take(name: KProperty1<TCurrent, P?>, withSeparateQuery: Boolean = false, thenFn: (includer: QIncluder<TStart, TQ, P>) -> Unit) {
        takeN(name.name, withSeparateQuery, thenFn)
    }

    fun <P> takeList(name: KProperty1<TCurrent, List<P>>, withSeparateQuery: Boolean = false, thenFn: (includer: QIncluder<TStart, TQ, P>) -> Unit) {
        takeN(name.name, withSeparateQuery, thenFn)
    }
}

inline fun <reified T, reified Q : TQRootBean<T, Q>> Q.include(includeFn: (includer: QIncluder<T, Q, T>) -> Unit): Q {
    val includer = QIncluder<T, Q, T>(this, null)
    includeFn(includer)
    return includer.resultingQuery
}
