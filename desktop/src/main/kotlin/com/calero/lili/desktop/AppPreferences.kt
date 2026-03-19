package com.calero.lili.desktop

import java.io.File
import java.util.Properties

/**
 * Persiste las preferencias de la aplicación en:
 *   ~\.calero\preferences.properties
 */
object AppPreferences {

    private val file: File by lazy {
        val dir = File(System.getProperty("user.home"), ".calero")
        if (!dir.exists()) dir.mkdirs()
        File(dir, "preferences.properties")
    }

    private fun load(): Properties {
        val props = Properties()
        if (file.exists()) {
            file.inputStream().use { props.load(it) }
        }
        return props
    }

    private fun save(props: Properties) {
        file.outputStream().use {
            props.store(it, "Calero Desktop Preferences")
        }
    }

    // ── Empresa seleccionada ──────────────────────────────────────────────────

    /** Devuelve el idEmpresa guardado, o null si no hay ninguno. */
    fun getIdEmpresa(): Long? {
        return load().getProperty("idEmpresa")?.toLongOrNull()
    }

    /** Devuelve el nombre (razonSocial) guardado. */
    fun getRazonSocial(): String? {
        return load().getProperty("razonSocial")?.takeIf { it.isNotBlank() }
    }

    /** Persiste idEmpresa + razonSocial. */
    fun guardarEmpresa(idEmpresa: Long, razonSocial: String) {
        val props = load()
        props.setProperty("idEmpresa",   idEmpresa.toString())
        props.setProperty("razonSocial", razonSocial)
        save(props)
    }

    /** Borra la empresa guardada (fuerza volver al selector). */
    fun limpiarEmpresa() {
        val props = load()
        props.remove("idEmpresa")
        props.remove("razonSocial")
        save(props)
    }
}
