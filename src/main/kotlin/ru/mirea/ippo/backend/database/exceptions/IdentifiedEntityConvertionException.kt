package ru.mirea.ippo.backend.database.exceptions

class IdentifiedEntityConvertionException(objectName: String)
    : Exception("Cannot convert identified dto \"${objectName}\" to model - ID is null")