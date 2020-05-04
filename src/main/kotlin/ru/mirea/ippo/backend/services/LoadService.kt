package ru.mirea.ippo.backend.services

import org.jxls.common.Context
import org.jxls.util.JxlsHelper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.mirea.ippo.backend.database.entities.DbLoadUnitDistributedPart
import ru.mirea.ippo.backend.errorhandling.ObjectNotFoundException
import ru.mirea.ippo.backend.models.Form13LoadUnit
import ru.mirea.ippo.backend.models.LoadUnit
import ru.mirea.ippo.backend.models.LoadUnitDistributedPart
import ru.mirea.ippo.backend.repositories.DistributedLoadRepository
import ru.mirea.ippo.backend.repositories.LecturerRepository
import ru.mirea.ippo.backend.repositories.LoadRepository
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

@Service
class LoadService(
    val loadRepository: LoadRepository,
    val distributedLoadRepository: DistributedLoadRepository,
    val lecturerRepository: LecturerRepository
) {

    fun findAll(): List<LoadUnit> {
        return loadRepository.findAll().map { it.toModel() }
    }

    fun findDistributedParts(id: UUID): List<LoadUnitDistributedPart> {
        return distributedLoadRepository.findAllByLoadUnitId(id).map { it.toModel() }
    }

    fun findById(id: UUID): LoadUnit {
        return loadRepository.findByIdOrNull(id)?.toModel() ?: throw ObjectNotFoundException("LoadUnit", id)
    }

    fun deleteLoadUnit(id: UUID) {
        loadRepository.deleteById(id)
    }

    fun deleteLoadUnitDistributedPart(id: UUID, partId: UUID) {
        distributedLoadRepository.deleteById(partId)
    }

    fun addOrUpdatePart(id: UUID, partId: UUID?, lecturerId: UUID, load: BigDecimal) {
        val loadUnitDistributedPart = distributedLoadRepository.save(DbLoadUnitDistributedPart.fromTemplate(id, partId, lecturerId, load))
        distributedLoadRepository.refresh(loadUnitDistributedPart)
    }

    fun createForm13(): File{
        val loadUnits: ArrayList<Form13LoadUnit> = ArrayList()
        lecturerRepository.findAll().map { it.toModel() }.forEach { lecturer ->
            val lecturerLoadParts = distributedLoadRepository.findAllByLecturerId(lecturer.id).map { it.toModel() }
            val lecturerLoad = loadRepository.findAllByIdIn(lecturerLoadParts.map { it.loadUnitId }).map { it.toModel() }
            val lecturerLoadBySubject = lecturerLoad.groupBy { it.toKeyWithWithCourseAndSemester() }
            val lecturerLoadUnits = lecturerLoadBySubject.map {
                Form13LoadUnit(
                    it.key.subject,
                    it.key.course,
                    if (it.key.semester%2 == 1) "о" else "в",
                    it.value.map { listOfNotNull(it.group?.code).plus((it.stream?.groups?.map { it.code }?.toList() ?: emptyList())).distinct() }.flatten().distinct().joinToString("\n"),
                    it.value.filter { it.hoursType.equals("LECTURE") }.count(),
                    it.value.filter { it.hoursType.equals("PRACTICAL_CLASS") }.count(),
                    it.value.filter { it.hoursType.equals("LABORATORY_WORK") }.count(),
                    it.value.map { listOfNotNull(it.group).plus((it.stream?.groups ?: emptyList())).distinct() }.flatten().distinct().map { it.studentsNumber }.sum(),
                    it.value.find { it.hoursType.equals("LECTURE") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == lecturer.id }?.loadPart,
                    it.value.find { it.hoursType.equals("PRACTICAL_CLASS") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == lecturer.id }?.loadPart,
                    it.value.find { it.hoursType.equals("LABORATORY_WORK") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == lecturer.id }?.loadPart,
                    it.value.find { it.hoursType.equals("TEST") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == lecturer.id }?.loadPart,
                    it.value.find { it.hoursType.equals("EXAM") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == lecturer.id }?.loadPart,
                    it.value.find { it.hoursType.equals("COURSEWORK") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == lecturer.id }?.loadPart,
                    it.value.find { it.hoursType.equals("COURSE_PROJECT") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == lecturer.id }?.loadPart,
                    "${lecturer.lastName} ${lecturer.name.first()}. ${lecturer.middleName.first()}."
                )
            }
            loadUnits.addAll(lecturerLoadUnits)
        }
        val fileName = "Форма 13.xlsx"
        val fileOutputStream = FileOutputStream(fileName)
        val template = File("Form13Template.xlsx")
        val jxlsContext = Context()
        val templateStream: InputStream = template.inputStream()
        jxlsContext.putVar("loadUnits", loadUnits)
        JxlsHelper.getInstance().processTemplate(templateStream, fileOutputStream, jxlsContext)
        templateStream.close()
        fileOutputStream.close()
        val form13 = File("Форма 13.xlsx")
        return form13
    }

}