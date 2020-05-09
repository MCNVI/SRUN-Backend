package ru.mirea.ippo.backend.controllers.docs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import springfox.documentation.PathProvider
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.paths.RelativePathProvider
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import javax.servlet.ServletContext


@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    fun api(): Docket{
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("ru.mirea.ippo.backend.controllers"))
            .paths(PathSelectors.any())
            .build()
    }

}