package ru.mirea.ippo.backend.database.entities

interface Identified<T> {
    val id: T
}

interface Insertable<This, Id> : Identified<Id?> {
    fun clone(): This
}