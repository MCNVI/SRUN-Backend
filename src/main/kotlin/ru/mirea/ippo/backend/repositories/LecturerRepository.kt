package ru.mirea.ippo.backend.repositories

import io.ebean.Database
import io.ebean.Query
import org.springframework.stereotype.Component
import ru.mirea.ippo.backend.database.*
import ru.mirea.ippo.backend.database.entities.DbInsertableLecturer
import ru.mirea.ippo.backend.database.entities.DbLecturer
import ru.mirea.ippo.backend.database.entities.query.QDbInsertableLecturer
import ru.mirea.ippo.backend.database.entities.query.QDbLecturer
import ru.mirea.ippo.backend.models.Lecturer
import ru.mirea.ippo.backend.models.LecturerTemplate
import java.util.*

@Component
class LecturerRepository(val db: Database) {

    private fun Database.lecturers(): Query<DbLecturer> =
        this.eFind<DbLecturer>()
            .setDisableLazyLoading(true)
            .include {
                it.take(DbLecturer::lecturerType)
            }

    fun create(lecturer: LecturerTemplate): Lecturer{
        val lecturerId = db.eInsert(DbInsertableLecturer.fromTemplate(lecturer))
        return db.lecturers().eFindByIdOrDie(lecturerId).toModel()
    }

    fun getLecturers(): List<Lecturer> {
        return db.lecturers()
            .findList()
            .map { it.toModel() }
    }

    fun find(lecturerId: UUID) : Lecturer {
        return db.lecturers().eFindByIdOrDie(lecturerId).toModel()
    }

    fun update(lecturerId: UUID, lecturer: LecturerTemplate) : Lecturer {
        db.update(DbInsertableLecturer.fromTemplate(lecturer))
        return db.lecturers().eFindByIdOrDie(lecturerId).toModel()
    }

    fun delete(lecturerId: UUID) {
        QDbLecturer(db)
            .where()
            .id.eq(lecturerId)
            .delete()
    }



}