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

    implementation("org.springframework.boot:spring-boot-starter-web")

    //validaciones
    implementation("org.springframework.boot:spring-boot-starter-validation")

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



    // GOOGLE CLOUD
    implementation("com.google.cloud:google-cloud-secretmanager:2.31.0")
    implementation("com.google.cloud:google-cloud-storage:2.22.2")
    implementation("com.google.guava:guava:33.4.0-jre")
    implementation("com.google.inject:guice:7.0.0")
    implementation("com.google.cloud.sql:postgres-socket-factory:1.20.0")

    // JAXB / XML - Documentos electronicos SRI
    implementation("com.sun.xml.ws:jaxws-ri:4.0.3")

    // Apache XML Security - Firma digital XAdES
    implementation("org.apache.santuario:xmlsec:4.0.4")

    // BouncyCastle - Criptografia / Firma digital XML
    implementation("org.bouncycastle:bcpkix-jdk18on:1.81")
    implementation("org.bouncycastle:bcprov-jdk18on:1.81")

    // JAXB (solo Jakarta)
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    runtimeOnly("org.glassfish.jaxb:jaxb-runtime:4.0.5")

    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testImplementation("org.slf4j:slf4j-simple:2.0.17")

}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs += listOf("-Xjvm-default=all")
    }
}
