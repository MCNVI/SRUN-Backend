package ru.mirea.ippo.backend.repositories

import io.ebean.Database
import org.springframework.stereotype.Component
import ru.mirea.ippo.backend.database.eInsertOrUpdate
import ru.mirea.ippo.backend.database.entities.DbInsertableLecturerType
import ru.mirea.ippo.backend.database.entities.DbLecturerType
import ru.mirea.ippo.backend.database.entities.query.QDbLecturerType
import ru.mirea.ippo.backend.models.LecturerType
import ru.mirea.ippo.backend.models.LecturerTypeTemplate
import java.util.*

@Component
class DirectoryRepository(val db: Database) {

    fun getLecturersTypes(): List<LecturerType> =
        QDbLecturerType(db)
            .setDisableLazyLoading(true)
            .findList()
            .map { it.toModel() }

    fun updateLecturersTypes(lecturerTypes: List<LecturerTypeTemplate>) : List<LecturerType> {
        for (lecturerType in lecturerTypes) {
            val insertableType = DbInsertableLecturerType.fromTemplate(lecturerType)
            db.eInsertOrUpdate(insertableType)
        }
        return getLecturersTypes()
    }

    fun deleteLecturerType(lecturerTypeId: UUID) : List<LecturerType>{
        QDbLecturerType(db)
            .where()
            .id.eq(lecturerTypeId)
            .delete()

        return getLecturersTypes()
    }

}
