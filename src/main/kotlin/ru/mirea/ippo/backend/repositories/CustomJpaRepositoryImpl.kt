package ru.mirea.ippo.backend.repositories

import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import java.io.Serializable
import javax.persistence.EntityManager
import javax.transaction.Transactional

class CustomJpaRepositoryImpl<T, ID : Serializable> : SimpleJpaRepository<T, ID>, CustomJpaRepository<T, ID> {

    final val entityManager: EntityManager

    constructor(entityInformation: JpaEntityInformation<T, *>, entityManager: EntityManager) : super(
        entityInformation,
        entityManager
    ) {
        this.entityManager = entityManager
    }


    @Transactional
    override fun refresh(t: T) {
        entityManager.refresh(t)
    }

    override fun refreshAll(t: List<T>) {
        for (t in t){
            entityManager.refresh(t)
        }
    }
}