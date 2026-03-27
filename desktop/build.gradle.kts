import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
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

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":core"))

    // Spring Boot (sin web — solo contexto Spring para DI/JPA)
    implementation("org.springframework.boot:spring-boot-starter")

    // Compose Desktop
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    // Jackson (para leer db-config.json)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    // Coroutines (para la UI desktop)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs += listOf("-Xjvm-default=all")
    }
}

compose.desktop {
    application {
        mainClass = "com.calero.lili.desktop.DesktopAppKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)

            // Módulos JVM mínimos necesarios para esta app
            // (Compose Desktop + Spring Boot + PostgreSQL JDBC + HTTP client)
            // Usar includeAllModules = true empaqueta los 70+ módulos del JDK completo
            // y dispara el tiempo de inicio varios minutos innecesariamente.
            modules(
                "java.base", "java.desktop", "java.instrument",
                "java.logging", "java.management", "java.naming",
                "java.net.http", "java.prefs", "java.rmi",
                "java.security.jgss", "java.security.sasl",
                "java.sql", "java.xml",
                "jdk.crypto.cryptoki", "jdk.crypto.ec",
                "jdk.net", "jdk.unsupported"
            )

            packageName    = "Calero Lili"
            packageVersion = "1.0.0"
            description    = "Calero Lili Desktop"
            vendor         = "Calero"

            windows {
                upgradeUuid    = "A1B2C3D4-E5F6-7890-ABCD-EF1234567890"
                menuGroup      = "Calero"
                dirChooser     = true
                perUserInstall = true
                shortcut       = true
                menu           = true
            }
        }

        jvmArgs(
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
            "-Xms64m",                   // heap inicial reducido — crece según demanda
            "-Xmx512m",                  // límite máximo razonable para app de escritorio
            "-XX:TieredStopAtLevel=1"    // solo JIT C1 — arranque ~40 % más rápido
        )
    }
}

// El bootJar no se usa para desktop (se usa compose run), pero lo dejamos habilitado
// para que Spring Boot procese las dependencias correctamente
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}
tasks.named<Jar>("jar") {
    enabled = true
}
