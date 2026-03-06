import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")
    kotlin("plugin.lombok")
    id("io.spring.dependency-management")
}

group = "com.calero"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain(21)
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    // Spring Boot (sin web, solo JPA + core)
    api("org.springframework.boot:spring-boot-starter-data-jpa")

    // Base de datos
    runtimeOnly("org.postgresql:postgresql")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

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
    implementation("net.sf.jasperreports:jasperreports:6.20.1") {
        exclude(group = "com.github.librepdf", module = "openpdf")
    }

    // StringTemplate
    implementation("org.antlr:ST4:4.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs += listOf("-Xjvm-default=all")
    }
}
