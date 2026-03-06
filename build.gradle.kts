plugins {
    kotlin("jvm") version "1.9.23" apply false
    kotlin("plugin.lombok") version "1.9.23" apply false
    id("org.springframework.boot") version "3.3.0" apply false
    id("io.spring.dependency-management") version "1.1.5" apply false
    id("org.jetbrains.compose") version "1.6.1" apply false
}

group = "com.calero"
version = "0.0.1-SNAPSHOT"
description = "Software Contabilidad"

subprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
