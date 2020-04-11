package ru.mirea.ippo.backend.services

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.springframework.stereotype.Service
import ru.mirea.ippo.backend.database.entities.DbCurriculumShort
import ru.mirea.ippo.backend.database.entities.DbCurriculumUnit
import ru.mirea.ippo.backend.errorhandling.ValidationException
import ru.mirea.ippo.backend.models.Curriculum
import ru.mirea.ippo.backend.models.CurriculumShort
import ru.mirea.ippo.backend.models.CurriculumSubject
import ru.mirea.ippo.backend.models.CurriculumUnit
import ru.mirea.ippo.backend.repositories.CurriculumRepository
import ru.mirea.ippo.backend.repositories.CurriculumShortRepository
import java.io.File
import java.io.FileInputStream
import java.util.*

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
                            curriculum.id
                        )
                        val curriculumUnit =
                            curriculumRepository.save(DbCurriculumUnit.fromTemplate(curriculumUnitTemplate))
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
                            curriculum.id
                        )
                        val curriculumUnit =
                            curriculumRepository.save(DbCurriculumUnit.fromTemplate(curriculumUnitTemplate))
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
                            curriculum.id
                        )
                        val curriculumUnit =
                            curriculumRepository.save(DbCurriculumUnit.fromTemplate(curriculumUnitTemplate))
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
                                curriculum.id
                            )
                            val curriculumUnit =
                                curriculumRepository.save(DbCurriculumUnit.fromTemplate(curriculumUnitTemplate))
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
                                curriculum.id
                            )
                            val curriculumUnit =
                                curriculumRepository.save(DbCurriculumUnit.fromTemplate(curriculumUnitTemplate))
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
                                curriculum.id
                            )
                            val curriculumUnit =
                                curriculumRepository.save(DbCurriculumUnit.fromTemplate(curriculumUnitTemplate))
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
                                curriculum.id
                            )
                            val curriculumUnit =
                                curriculumRepository.save(DbCurriculumUnit.fromTemplate(curriculumUnitTemplate))
                            curriculumRepository.refresh(curriculumUnit)
                        }
                    }
                }
            }
        }


    }

}