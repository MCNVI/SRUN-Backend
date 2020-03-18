package ru.mirea.ippo.backend.database

import io.ebean.Database
import io.ebean.Query
import ru.mirea.ippo.backend.database.entities.Identified
import ru.mirea.ippo.backend.database.entities.Insertable
import ru.mirea.ippo.backend.errorhandling.ObjectNotFoundException

inline fun <reified T> Database.eFind(): Query<T> {
    return this.find(T::class.java)
}

inline fun <reified T : Insertable<T, Id>, reified Id> Database.eInsert(t: T): Id {
    val clone = t.clone()
    this.insert(clone)
    return clone.id!!
}

inline fun <reified T : Insertable<T, Id>, reified Id> Database.eInsertOrUpdate(t: T): Id {
    if(t.id != null){
        val clone = t.clone()
        this.merge(clone)
        return clone.id!!
    }
    else {
        val clone = t.clone()
        this.insert(clone)
        return clone.id!!
    }
}

inline fun <reified T : Identified<Id>, reified Id : Any> Query<T>.eFindById(id: Id): T? {
    return this.setId(id).findList().firstOrNull()
}

inline fun <reified T : Identified<Id>, reified Id : Any> Query<T>.eFindByIdOrDie(id: Id): T {
    return this.setId(id).findList().firstOrNull() ?: throw ObjectNotFoundException(T::class.simpleName ?: "unknown object", id)
}
