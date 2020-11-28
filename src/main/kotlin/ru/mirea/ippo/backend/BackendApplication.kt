package ru.mirea.ippo.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.mirea.ippo.backend.repositories.CustomJpaRepositoryImpl

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl::class)
class BackendApplication()
fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}
