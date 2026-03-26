package com.calero.lili.desktop.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import java.io.File
import java.nio.file.Files

class DbConfigEnvironmentPostProcessor : EnvironmentPostProcessor {

    private val configDir  = System.getProperty("user.dir")
    private val configFile = "$configDir\\db-config.json"

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        val file = File(configFile)

        if (!file.exists()) {
            crearPlantilla(file)
            throw IllegalStateException(
                "Archivo de configuracion no encontrado: $configFile\n" +
                "Se ha creado una plantilla en esa ruta. " +
                "Edite el archivo con los datos de su base de datos y reinicie la aplicacion."
            )
        }

        try {
            val config = jacksonObjectMapper().readValue(file, DbConfig::class.java)
            val props = mapOf(
                "spring.datasource.url"      to config.url,
                "spring.datasource.username" to config.username,
                "spring.datasource.password" to config.password
            )
            environment.propertySources.addFirst(MapPropertySource("externalDbConfig", props))
        } catch (e: Exception) {
            throw IllegalStateException("Error al leer la configuracion de base de datos desde: $configFile", e)
        }
    }

    private fun crearPlantilla(file: File) {
        val template = """{
  "url": "jdbc:postgresql://localhost:5432/nombre_base_de_datos",
  "username": "postgres",
  "password": "su_contraseña"
}
"""
        runCatching {
            Files.writeString(file.toPath(), template)
        }
    }
}
