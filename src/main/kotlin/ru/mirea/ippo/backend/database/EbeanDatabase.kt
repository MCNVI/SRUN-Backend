package ru.mirea.ippo.backend.database

import com.fasterxml.jackson.databind.ObjectMapper
import io.ebean.EbeanServer
import io.ebean.EbeanServerFactory
import io.ebean.annotation.Platform
import io.ebean.config.ServerConfig
import io.ebean.config.dbplatform.DbType
import org.springframework.beans.factory.FactoryBean
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class MyEbeanServerFactory(private val mapper: ObjectMapper, private val dataSource: DataSource) : FactoryBean<EbeanServer> {
    override fun getObject(): EbeanServer? = createEbeanServer()

    override fun getObjectType(): Class<*>? = EbeanServer::class.java
    override fun isSingleton() = true

    fun createEbeanServer(): EbeanServer {
        val config = ServerConfig().also {
            it.addPackage("ru.mirea.ippo.backend.database.entities")
            //it.addPackage("ru.mirea.ippo.backend.database.dto.characteristics")
            //it.addPackage("ru.mirea.ippo.backend.models.sharedDto")
            it.dataSource = dataSource
            it.objectMapper = mapper
            it.addCustomMapping(DbType.DECIMAL, "decimal(19,5)", Platform.POSTGRES)
            it.isDefaultServer = true
        }

        return EbeanServerFactory.create(config)
    }
}
