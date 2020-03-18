package ru.mirea.ippo.backend.repositories

import io.ebean.Database
import org.springframework.stereotype.Component
import ru.mirea.ippo.backend.database.entities.query.QDbLecturerType
import ru.mirea.ippo.backend.models.LecturerType

@Component
class DirectoryRepository(val db: Database) {

    fun getLecturersTypes(): List<LecturerType> =
        QDbLecturerType(db)
            .setDisableLazyLoading(true)
            .findList()
            .map { it.toModel() }
}
