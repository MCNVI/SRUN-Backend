//package ru.mirea.ippo.backend.database.entities
//
//import org.hibernate.annotations.GenericGenerator
//import ru.mirea.ippo.backend.models.Lecturer
//import ru.mirea.ippo.backend.models.LecturerTemplate
//import java.math.BigDecimal
//import java.util.*
//import javax.persistence.*
//
//@Entity
//@Table(schema = "ippo", name = "staffing_table")
//data class DbLecturer (
//    @Id
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid", strategy = "uuid2")
//    val id: UUID?,
//    val lecturerTypeId: UUID,
//    @ManyToOne
//    val lecturerType: DbLecturerType,
//    val name: String,
//    val middleName: String,
//    val lastName: String,
//    val lecturerRate: BigDecimal,
//    val lecturerHoursForRate: BigDecimal,
//    val lecturerMaxHoursForRate: BigDecimal
//) {
//    fun toModel(): Lecturer = Lecturer(
//        id!!,
//        lecturerType.toModel(),
//        name,
//        middleName,
//        lastName,
//        lecturerRate,
//        lecturerHoursForRate,
//        lecturerMaxHoursForRate
//    )
//    companion object{
//        fun fromTemplate(lecturer: LecturerTemplate) = DbLecturer(
//            lecturer.id,
//            lecturer.lecturerTypeId,
//            type.hours,
//            type.isPartTime,
//            type.isExternal ?: false
//        )
//    }
//}