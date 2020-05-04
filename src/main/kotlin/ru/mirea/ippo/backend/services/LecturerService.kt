package ru.mirea.ippo.backend.services

import org.jxls.common.Context
import org.jxls.util.JxlsHelper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.mirea.ippo.backend.database.entities.DbLecturer
import ru.mirea.ippo.backend.errorhandling.ObjectNotFoundException
import ru.mirea.ippo.backend.models.Lecturer
import ru.mirea.ippo.backend.models.LecturerLoadUnit
import ru.mirea.ippo.backend.models.LecturerTemplate
import ru.mirea.ippo.backend.models.PrioritizedLecturer
import ru.mirea.ippo.backend.repositories.DirectoryRepository
import ru.mirea.ippo.backend.repositories.DistributedLoadRepository
import ru.mirea.ippo.backend.repositories.LecturerRepository
import ru.mirea.ippo.backend.repositories.LoadRepository
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.math.BigDecimal
import java.util.*

@Service
class LecturerService(
    val lecturerRepository: LecturerRepository,
    val directoryRepository: DirectoryRepository,
    val loadRepository: LoadRepository,
    val distributedLoadRepository: DistributedLoadRepository
) {

    fun find(id: UUID): Lecturer =
        lecturerRepository.findByIdOrNull(id)?.toModel() ?: throw ObjectNotFoundException("Lecturer", id)

    fun findAll(): List<Lecturer> = lecturerRepository.findAll().map { it.toModel() }

    fun getByRelevance(loadUnitId: UUID): List<PrioritizedLecturer> {
        val loadUnit = loadRepository.findByIdOrNull(loadUnitId)?.toModel() ?: throw ObjectNotFoundException(
            "LoadUnit",
            loadUnitId
        )
        return emptyList()
    }

    fun createIndividualPlan(id: UUID) {
        val lecturerInfo = find(id)
        val lecturerLoadParts = distributedLoadRepository.findAllByLecturerId(id).map { it.toModel() }
        val lecturerLoad = loadRepository.findAllByIdIn(lecturerLoadParts.map { it.loadUnitId }).map { it.toModel() }
        val lecturerLoadBySubjectAutumn = lecturerLoad.filter { it.semester%2 == 1 }.groupBy { it.toKey() }
        val lecturerLoadBySubjectSpring = lecturerLoad.filter { it.semester%2 == 0 }.groupBy { it.toKey() }
        val lecturerLoadUnitsAutumn = lecturerLoadBySubjectAutumn.map {
            LecturerLoadUnit(
                it.key.subject,
                it.value.map { listOfNotNull(it.group?.code).plus((it.stream?.groups?.map { it.code }?.toList() ?: emptyList())).distinct() }.flatten().distinct().joinToString("\n"),
                it.value.map { listOfNotNull(it.group).plus((it.stream?.groups ?: emptyList())).distinct() }.flatten().distinct().map { it.studentsNumber }.sum(),
                it.value.find { it.hoursType.equals("LECTURE") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart,
                it.value.find { it.hoursType.equals("PRACTICAL_CLASS") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart,
                it.value.find { it.hoursType.equals("LABORATORY_WORK") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart,
                it.value.find { it.hoursType.equals("TEST") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart,
                it.value.find { it.hoursType.equals("EXAM") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart,
                it.value.find { it.hoursType.equals("COURSEWORK") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart,
                it.value.find { it.hoursType.equals("COURSE_PROJECT") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart
            )
        }
        val lecturerLoadUnitsSpring = lecturerLoadBySubjectSpring.map {
            LecturerLoadUnit(
                it.key.subject,
                it.value.map { listOfNotNull(it.group?.code).plus((it.stream?.groups?.map { it.code }?.toList() ?: emptyList())).distinct() }.flatten().distinct().joinToString("\n"),
                it.value.map { listOfNotNull(it.group).plus((it.stream?.groups ?: emptyList())).distinct() }.flatten().distinct().map { it.studentsNumber }.sum(),
                it.value.find { it.hoursType.equals("LECTURE") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart,
                it.value.find { it.hoursType.equals("PRACTICAL_CLASS") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart,
                it.value.find { it.hoursType.equals("LABORATORY_WORK") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart,
                it.value.find { it.hoursType.equals("TEST") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart,
                it.value.find { it.hoursType.equals("EXAM") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart,
                it.value.find { it.hoursType.equals("COURSEWORK") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart,
                it.value.find { it.hoursType.equals("COURSE_PROJECT") }?.loadUnitPartDistributedParts?.find { it.lecturer?.id == id }?.loadPart
            )
        }
        println(lecturerLoadUnitsAutumn.joinToString("\n"))
        val fileName = "${lecturerInfo.getFio()} (${lecturerInfo.lecturerRate.stripTrailingZeros()}ст).xlsx"
        val fileOutputStream = FileOutputStream(fileName)
        val template = File("ExcelTemplateIP.xlsx")
        val jxlsContext = Context()
        val templateStream: InputStream = template.inputStream()
        //Титул
        jxlsContext.putVar("lecturers", listOf(lecturerInfo))
        //Осенний семестр
        jxlsContext.putVar("lecturerLoadAutumn", lecturerLoadUnitsAutumn)
        //Весенний семестр
        jxlsContext.putVar("lecturerLoadSpring", lecturerLoadUnitsSpring)
        JxlsHelper.getInstance().processTemplate(templateStream, fileOutputStream, jxlsContext)
        templateStream.close()
        fileOutputStream.close()
    }

    fun createOrUpdate(lecturerTemplate: LecturerTemplate): Lecturer {
        val lecturerType = directoryRepository.findByIdOrNull(lecturerTemplate.lecturerTypeId)
            ?: throw ObjectNotFoundException("LecturerType", lecturerTemplate.lecturerTypeId)
        val lecturerLoadForRate =
            BigDecimal.valueOf(lecturerTemplate.monthAmount.toDouble() / 10 * lecturerTemplate.lecturerRate.toDouble() * lecturerType.studyLoad)
        val lecturerMaxLoadForRate =
            BigDecimal.valueOf(lecturerTemplate.monthAmount.toDouble() / 10 * lecturerTemplate.lecturerRate.toDouble() * 900)
        val dbLecturer = DbLecturer.fromTemplate(lecturerTemplate, lecturerLoadForRate, lecturerMaxLoadForRate)

        val lecturer = lecturerRepository.save(dbLecturer)
        lecturerRepository.refresh(lecturer)

        return lecturerRepository.findByIdOrNull(lecturer.id)?.toModel() ?: throw ObjectNotFoundException(
            "Lecturer",
            lecturer.id
        )
    }

    fun delete(id: UUID) {
        if (lecturerRepository.findByIdOrNull(id) != null)
            lecturerRepository.deleteById(id)
        else throw ObjectNotFoundException("Lecturer", id)
    }

}