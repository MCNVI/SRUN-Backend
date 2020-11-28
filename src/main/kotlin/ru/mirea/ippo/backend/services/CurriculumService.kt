package ru.mirea.ippo.backend.services

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.mirea.ippo.backend.database.entities.DbCurriculumShort
import ru.mirea.ippo.backend.database.entities.DbCurriculumUnit
import ru.mirea.ippo.backend.database.entities.DbUser
import ru.mirea.ippo.backend.errorhandling.ObjectNotFoundException
import ru.mirea.ippo.backend.errorhandling.ValidationException
import ru.mirea.ippo.backend.models.*
import ru.mirea.ippo.backend.repositories.CurriculumRepository
import ru.mirea.ippo.backend.repositories.CurriculumShortRepository
import ru.mirea.ippo.backend.repositories.UserRepository
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.annotation.PostConstruct

@Service
class CurriculumService(
    val curriculumRepository: CurriculumRepository,
    val curriculumShortRepository: CurriculumShortRepository
) {

    fun findAll(): List<CurriculumShort> = curriculumShortRepository.findAll().map { it.toModel() }.distinct()

    fun find(curriculumId: UUID): Curriculum {
        val curriculumUnits = curriculumRepository.findAllByCurriculumId(
            curriculumId
        ).map { it.toModel() }
        val subjects = curriculumUnits.groupBy { it.toKey() }
            .map { CurriculumSubject(it.key.course, it.key.semester, it.key.subject, it.value) }
        return Curriculum(subjects)
    }

    fun createFromFile(curriculum: File) {
        val curriculumInputStream: FileInputStream = curriculum.inputStream()
        val workbook = HSSFWorkbook(curriculumInputStream)
        val mainSheet = workbook.getSheet("Титул")
        val fieldOfStudy = mainSheet.getRow(17).getCell(1).stringCellValue
        val educationalProfile = mainSheet.getRow(18).getCell(1).stringCellValue
        val startYear = mainSheet.getRow(28).getCell(19).stringCellValue.toInt()
        val isExists = curriculumShortRepository.existsByFieldOfStudyAndEducationalProfileAndStartYear(
            fieldOfStudy,
            educationalProfile,
            startYear
        )
        if(isExists) throw ValidationException(listOf("Загружаемый план уже существует"))

        val curriculum =
            curriculumShortRepository.save(
                DbCurriculumShort.fromTemplate(
                    CurriculumShort(
                        null,
                        fieldOfStudy,
                        educationalProfile,
                        startYear
                    )
                )
            )
        curriculumShortRepository.refresh(curriculum)

        val planSheet = workbook.getSheet("План")
        for (row in planSheet) {
            if (row.getCell(0).stringCellValue.equals("+") && row.getCell(row.lastCellNum.toInt() - 3).stringCellValue.isNotEmpty()) {
                val subject = row.getCell(2).stringCellValue
                val department = row.getCell(79).stringCellValue
                for (cell in row) {
                    if (cell.stringCellValue.isNotEmpty() && planSheet.getRow(2).getCell(cell.columnIndex).stringCellValue.equals(
                            "Лек"
                        )
                    ) {
                        val course =
                            planSheet.getRow(0).getCell(cell.columnIndex - 2).stringCellValue.filter { it.isDigit() }.toShortOrNull()
                                ?: planSheet.getRow(0).getCell(cell.columnIndex - 10).stringCellValue.filter { it.isDigit() }.toShort()
                        val semester =
                            planSheet.getRow(1).getCell(cell.columnIndex - 2).stringCellValue.filter { it.isDigit() }
                                .toShort()
                        val curriculumUnitTemplate = CurriculumUnit(
                            null,
                            course,
                            semester,
                            subject,
                            "LECTURE",
                            cell.stringCellValue.toBigDecimal(),
                            curriculum.id,
                            department.toInt()
                        )
                        val curriculumUnit =
                            curriculumRepository.save(DbCurriculumUnit.fromModel(curriculumUnitTemplate))
                        curriculumRepository.refresh(curriculumUnit)
                    }
                    if (cell.stringCellValue.isNotEmpty() && planSheet.getRow(2).getCell(cell.columnIndex).stringCellValue.equals(
                            "Лаб"
                        )
                    ) {
                        val course =
                            planSheet.getRow(0).getCell(cell.columnIndex - 3).stringCellValue.filter { it.isDigit() }.toShortOrNull()
                                ?: planSheet.getRow(0).getCell(cell.columnIndex - 11).stringCellValue.filter { it.isDigit() }.toShort()
                        val semester =
                            planSheet.getRow(1).getCell(cell.columnIndex - 3).stringCellValue.filter { it.isDigit() }
                                .toShort()
                        val curriculumUnitTemplate = CurriculumUnit(
                            null,
                            course,
                            semester,
                            subject,
                            "LABORATORY_WORK",
                            cell.stringCellValue.toBigDecimal(),
                            curriculum.id,
                            department.toInt()
                        )
                        val curriculumUnit =
                            curriculumRepository.save(DbCurriculumUnit.fromModel(curriculumUnitTemplate))
                        curriculumRepository.refresh(curriculumUnit)
                    }
                    if (cell.stringCellValue.isNotEmpty() && planSheet.getRow(2).getCell(cell.columnIndex).stringCellValue.equals(
                            "Пр"
                        )
                    ) {
                        val course =
                            planSheet.getRow(0).getCell(cell.columnIndex - 4).stringCellValue.filter { it.isDigit() }.toShortOrNull()
                                ?: planSheet.getRow(0).getCell(cell.columnIndex - 12).stringCellValue.filter { it.isDigit() }.toShort()
                        val semester =
                            planSheet.getRow(1).getCell(cell.columnIndex - 4).stringCellValue.filter { it.isDigit() }
                                .toShort()
                        val curriculumUnitTemplate = CurriculumUnit(
                            null,
                            course,
                            semester,
                            subject,
                            "PRACTICAL_CLASS",
                            cell.stringCellValue.toBigDecimal(),
                            curriculum.id,
                            department.toInt()
                        )
                        val curriculumUnit =
                            curriculumRepository.save(DbCurriculumUnit.fromModel(curriculumUnitTemplate))
                        curriculumRepository.refresh(curriculumUnit)
                    }
                    if (cell.stringCellValue.isNotEmpty() && planSheet.getRow(2).getCell(cell.columnIndex).stringCellValue.contains(
                            "Экза"
                        )
                    ) {
                        cell.stringCellValue.chunked(1).forEach {
                            val semester = it.toShort()
                            val course = when (semester) {
                                1.toShort() -> 1.toShort()
                                2.toShort() -> 1.toShort()
                                3.toShort() -> 2.toShort()
                                4.toShort() -> 2.toShort()
                                5.toShort() -> 3.toShort()
                                6.toShort() -> 3.toShort()
                                7.toShort() -> 4.toShort()
                                8.toShort() -> 4.toShort()
                                else -> 0.toShort()
                            }
                            val curriculumUnitTemplate = CurriculumUnit(
                                null,
                                course,
                                semester,
                                subject,
                                "EXAM",
                                null,
                                curriculum.id,
                                department.toInt()
                            )
                            val curriculumUnit =
                                curriculumRepository.save(DbCurriculumUnit.fromModel(curriculumUnitTemplate))
                            curriculumRepository.refresh(curriculumUnit)
                        }
                    }
                    if (cell.stringCellValue.isNotEmpty() && planSheet.getRow(2).getCell(cell.columnIndex).stringCellValue.contains(
                            "Зачет"
                        )
                    ) {
                        cell.stringCellValue.chunked(1).forEach {
                            val semester = it.toShort()
                            val course = when (semester) {
                                1.toShort() -> 1.toShort()
                                2.toShort() -> 1.toShort()
                                3.toShort() -> 2.toShort()
                                4.toShort() -> 2.toShort()
                                5.toShort() -> 3.toShort()
                                6.toShort() -> 3.toShort()
                                7.toShort() -> 4.toShort()
                                8.toShort() -> 4.toShort()
                                else -> 0.toShort()
                            }
                            val curriculumUnitTemplate = CurriculumUnit(
                                null,
                                course,
                                semester,
                                subject,
                                "TEST",
                                null,
                                curriculum.id,
                                department.toInt()
                            )
                            val curriculumUnit =
                                curriculumRepository.save(DbCurriculumUnit.fromModel(curriculumUnitTemplate))
                            curriculumRepository.refresh(curriculumUnit)
                        }
                    }
                    if (cell.stringCellValue.isNotEmpty() && planSheet.getRow(2).getCell(cell.columnIndex).stringCellValue.contains(
                            "КР"
                        )
                    ) {
                        cell.stringCellValue.chunked(1).forEach {
                            val semester = it.toShort()
                            val course = when (semester) {
                                1.toShort() -> 1.toShort()
                                2.toShort() -> 1.toShort()
                                3.toShort() -> 2.toShort()
                                4.toShort() -> 2.toShort()
                                5.toShort() -> 3.toShort()
                                6.toShort() -> 3.toShort()
                                7.toShort() -> 4.toShort()
                                8.toShort() -> 4.toShort()
                                else -> 0.toShort()
                            }
                            val curriculumUnitTemplate = CurriculumUnit(
                                null,
                                course,
                                semester,
                                subject,
                                "COURSEWORK",
                                null,
                                curriculum.id,
                                department.toInt()
                            )
                            val curriculumUnit =
                                curriculumRepository.save(DbCurriculumUnit.fromModel(curriculumUnitTemplate))
                            curriculumRepository.refresh(curriculumUnit)
                        }
                    }
                    if (cell.stringCellValue.isNotEmpty() && planSheet.getRow(2).getCell(cell.columnIndex).stringCellValue.contains(
                            "КП"
                        )
                    ) {
                        cell.stringCellValue.chunked(1).forEach {
                            val semester = it.toShort()
                            val course = when (semester) {
                                1.toShort() -> 1.toShort()
                                2.toShort() -> 1.toShort()
                                3.toShort() -> 2.toShort()
                                4.toShort() -> 2.toShort()
                                5.toShort() -> 3.toShort()
                                6.toShort() -> 3.toShort()
                                7.toShort() -> 4.toShort()
                                8.toShort() -> 4.toShort()
                                else -> 0.toShort()
                            }
                            val curriculumUnitTemplate = CurriculumUnit(
                                null,
                                course,
                                semester,
                                subject,
                                "COURSE_PROJECT",
                                null,
                                curriculum.id,
                                department.toInt()
                            )
                            val curriculumUnit =
                                curriculumRepository.save(DbCurriculumUnit.fromModel(curriculumUnitTemplate))
                            curriculumRepository.refresh(curriculumUnit)
                        }
                    }
                }
            }
        }


    }

    fun delete(curriculumId: UUID) {
        curriculumShortRepository.deleteById(curriculumId)
    }

    fun createOrUpdateUnit(curriculumId: UUID, curriculumUnit: CurriculumUnitTemplate): CurriculumUnit{
        if (curriculumUnit.id != null){
            curriculumRepository.deleteById(curriculumUnit.id)
        }
        val dbCurriculumUnit = DbCurriculumUnit.fromTemplate(curriculumUnit, curriculumId)
        val curriculumUnit = curriculumRepository.save(dbCurriculumUnit)
        curriculumRepository.refresh(curriculumUnit)
        return curriculumRepository.findByIdOrNull(curriculumUnit.id)?.toModel() ?: throw ObjectNotFoundException(
            "curriculumUnit",
            curriculumUnit.id
        )
    }

    fun deleteCurriculumUnit(curriculumId: UUID, curriculumUnitId: UUID){
        curriculumRepository.deleteById(curriculumUnitId)
    }

}