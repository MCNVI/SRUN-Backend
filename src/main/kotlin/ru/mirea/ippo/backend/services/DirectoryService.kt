package ru.mirea.ippo.backend.services

import org.springframework.stereotype.Service
import ru.mirea.ippo.backend.entities.DbLecturerType
import ru.mirea.ippo.backend.repositories.LecturerTypeRepository

@Service
class DirectoryService(val lecturerTypeRepository: LecturerTypeRepository) {
    fun getLecturerTypes(): List<DbLecturerType>{
        return lecturerTypeRepository.findAll()
    }

}