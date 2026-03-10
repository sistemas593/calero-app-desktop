plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.calero"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {

    implementation(project(":core"))
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // DevTools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Base de datos
    runtimeOnly("org.postgresql:postgresql")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // XML / Comprobantes electrónicos
    implementation("javax.xml.bind:jaxb-api:2.3.0")

    // Campos JSON en Hibernate
    implementation("com.vladmihalcea:hibernate-types-52:2.10.0")

    // Apache Commons
    implementation("org.apache.commons:commons-lang3:3.11")

    // Excel (carga masiva)
    implementation("org.apache.poi:poi:4.1.2")
    implementation("org.apache.poi:poi-ooxml:4.1.2")
    implementation("com.monitorjbl:xlsx-streamer:2.1.0")

    // PDF
    implementation("com.github.librepdf:openpdf:1.3.26")
    implementation("com.itextpdf:itextpdf:5.5.13.2")

    // Reportes
    implementation("net.sf.jasperreports:jasperreports:6.20.1")

    // Swagger / OpenAPI
    implementation("io.springfox:springfox-swagger2:2.9.2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // StringTemplate
    implementation("org.antlr:ST4:4.3")



}
