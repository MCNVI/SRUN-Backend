import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    java
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.spring") version "1.3.61"
    kotlin("plugin.jpa") version "1.3.61"
}

group = "ru.mirea.ippo.backend"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}


repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.vladmihalcea:hibernate-types-52:2.9.9")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core:6.1.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("io.springfox:springfox-swagger2:2.9.2")
    compile("io.springfox:springfox-swagger-ui:2.9.2")
    compile("org.apache.poi:poi-ooxml:4.1.2")
    compile("org.jxls:jxls:2.8.1")
    compile("org.jxls:jxls-poi:2.8.1")
    compile("com.auth0:java-jwt:3.10.3")


    testCompile("org.jetbrains.kotlin:kotlin-test")
    testCompile("org.jetbrains.kotlin:kotlin-test-junit")
    testCompile("org.mockito:mockito-junit-jupiter:2.23.0")
    testCompile("org.mockito:mockito-core:2.21.0")
    testCompile("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
