package com.calero.lili.desktop.ui.actualizacion

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.Duration
import java.util.zip.ZipInputStream

private const val FECHA_VERSION_LOCAL = "2026-03-26"
private const val API_URL = "https://api.facturador.com.ec/apist/v1.0/datos/fecha-actualizacion-sitacfacturador"

sealed class UpdateCheckState {
    object Idle                                        : UpdateCheckState()
    object Checking                                    : UpdateCheckState()
    object UpToDate                                    : UpdateCheckState()
    data class UpdateAvailable(val link: String)       : UpdateCheckState()
    object Descargando                                 : UpdateCheckState()
    object ListoParaReiniciar                          : UpdateCheckState()
    data class ErrorActualizacion(val mensaje: String) : UpdateCheckState()
    object Error                                       : UpdateCheckState()
}

class ActualizacionViewModel {

    private val _state = MutableStateFlow<UpdateCheckState>(UpdateCheckState.Idle)
    val state: StateFlow<UpdateCheckState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /** Inicia la verificación de actualizaciones. Llamar al hacer clic en "Continuar". */
    fun iniciar() {
        if (_state.value == UpdateCheckState.Idle) {
            verificarActualizacion()
        }
    }

    // ─── Verificación de versión ──────────────────────────────────────────────

    private fun verificarActualizacion() {
        scope.launch {
            try {
                val client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build()
                val request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build()
                val response = client.send(request, HttpResponse.BodyHandlers.ofString())
                val body     = response.body()
                val fechaApi = jsonField(body, "fechaActualizacion") ?: ""
                val link     = jsonField(body, "link") ?: ""

                if (fechaApi != FECHA_VERSION_LOCAL && link.isNotBlank()) {
                    _state.value = UpdateCheckState.UpdateAvailable(link)
                } else {
                    _state.value = UpdateCheckState.UpToDate
                }
            } catch (_: Exception) {
                // Sin conexión o fallo de red → continuar normal
                _state.value = UpdateCheckState.Error
            }
        }
    }

    // ─── Descarga y actualización ────────────────────────────────────────────

    /**
     * Proceso completo de actualización:
     * 1. Descarga el ZIP (siguiendo redirects de Mediafire u otros servicios)
     * 2. Valida que sea un ZIP real
     * 3. Extrae los JARs en carpeta temporal
     * 4. Detecta la carpeta "app" de la instalación jpackage
     * 5. Genera un .bat que reemplaza los JARs usando los nombres exactos
     *    del instalador (para que app.cfg siga funcionando)
     * 6. Lanza el .bat desacoplado y cierra la app
     */
    fun descargar(link: String) {
        scope.launch {
            try {
                _state.value = UpdateCheckState.Descargando

                // ── 1. Preparar carpeta temporal
                val tempDir = Path.of(System.getProperty("java.io.tmpdir"), "calero-update")
                Files.createDirectories(tempDir)
                val zipPath = tempDir.resolve("update.zip")

                // ── 2. Descargar ZIP — IMPORTANTE: followRedirects para Mediafire (302)
                val client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build()
                val request = HttpRequest.newBuilder()
                    .uri(URI.create(link))
                    .timeout(Duration.ofMinutes(10))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build()
                client.send(request, HttpResponse.BodyHandlers.ofFile(zipPath))

                // ── 3. Validar que el archivo descargado sea un ZIP real
                if (!esZipValido(zipPath)) {
                    _state.value = UpdateCheckState.ErrorActualizacion(
                        "El archivo descargado no es un ZIP válido.\n" +
                        "Verifique que el link de actualización sea una descarga directa."
                    )
                    return@launch
                }

                // ── 4. Extraer JARs
                val extractDir = tempDir.resolve("extracted")
                Files.createDirectories(extractDir)
                extraerZip(zipPath, extractDir)

                // Verificar que se extrajeron JARs
                val jarExtraidos = listarJars(extractDir)
                if (jarExtraidos.isEmpty()) {
                    _state.value = UpdateCheckState.ErrorActualizacion(
                        "El ZIP no contiene archivos .jar."
                    )
                    return@launch
                }

                // ── 5. Localizar la carpeta "app" de la instalación
                val appDir = encontrarCarpetaApp()

                if (appDir != null && Files.isDirectory(appDir)) {
                    // ── 6. Crear y lanzar el script de reemplazo
                    val batPath = crearScriptActualizacion(jarExtraidos, appDir)
                    ProcessBuilder("cmd.exe", "/c", "start", "", "/b", batPath.toString())
                        .start()
                    // Señal al UI: cerrar la app para que el bat tome control
                    _state.value = UpdateCheckState.ListoParaReiniciar
                } else {
                    // No instalado vía MSI (ej. ejecución desde IDE) → continuar normal
                    _state.value = UpdateCheckState.UpToDate
                }

            } catch (e: Exception) {
                _state.value = UpdateCheckState.ErrorActualizacion(
                    "Error al descargar la actualización:\n${e.message}"
                )
            }
        }
    }

    fun continuar() {
        _state.value = UpdateCheckState.UpToDate
    }

    fun onDestroy() = scope.cancel()

    // ─── Helpers privados ────────────────────────────────────────────────────

    /**
     * Detecta la carpeta "app" donde jpackage instala los JARs.
     *
     * Estructura típica de instalación con perUserInstall:
     *   C:\Users\<user>\AppData\Local\Calero Lili\
     *     ├── Calero Lili.exe
     *     ├── app\          ← aquí viven los JARs (con hashes en el nombre)
     *     └── runtime\      ← JVM incluido
     */
    private fun encontrarCarpetaApp(): Path? {
        val cmd    = ProcessHandle.current().info().command().orElse(null) ?: return null
        val exeDir = Path.of(cmd).parent ?: return null
        val appDir = exeDir.resolve("app")
        return if (Files.isDirectory(appDir)) appDir else null
    }

    /**
     * Verifica que el archivo sea un ZIP válido comprobando los magic bytes (PK).
     */
    private fun esZipValido(path: Path): Boolean {
        return try {
            if (!Files.exists(path) || Files.size(path) < 4) return false
            Files.newInputStream(path).use { stream ->
                val header = ByteArray(4)
                stream.read(header)
                // Magic bytes de ZIP: 0x50 0x4B 0x03 0x04
                header[0] == 0x50.toByte() && header[1] == 0x4B.toByte()
            }
        } catch (_: Exception) { false }
    }

    /**
     * Extrae todos los archivos del ZIP directamente en [destDir]
     * (ignora la estructura de subcarpetas internas).
     */
    private fun extraerZip(zipPath: Path, destDir: Path) {
        ZipInputStream(Files.newInputStream(zipPath)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) {
                    val destFile = destDir.resolve(Path.of(entry.name).fileName)
                    Files.copy(zis, destFile, StandardCopyOption.REPLACE_EXISTING)
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }

    /** Lista los JARs en una carpeta. */
    private fun listarJars(dir: Path): List<Path> =
        Files.list(dir).filter { it.fileName.toString().endsWith(".jar") }.toList()

    /**
     * Genera un .bat que reemplaza los JARs de la instalación preservando
     * el nombre exacto que usa app.cfg (el cual tiene un hash al final).
     *
     * Estrategia:
     *  - Busca en [appDir] el JAR cuyo nombre empieza con "desktop" o "core"
     *  - Busca en [nuevosJars] el JAR que contiene "desktop" o "core"
     *  - El BAT hace: copy /y "nuevo.jar" "appDir\viejo-con-hash.jar"
     *
     * Así app.cfg sigue apuntando al nombre correcto aunque el nuevo JAR
     * venga con un nombre diferente.
     */
    private fun crearScriptActualizacion(nuevosJars: List<Path>, appDir: Path): Path {
        val batPath = appDir.parent.resolve("update.bat")
        val exePath = ProcessHandle.current().info().command().orElse(null)

        // Buscar el JAR destino (con hash) en la carpeta app instalada
        val viejoDesktop = listarJars(appDir).find { it.fileName.toString().startsWith("desktop") }
        val viejoCore    = listarJars(appDir).find { it.fileName.toString().startsWith("core") }

        // Buscar los JARs nuevos extraídos del ZIP
        val nuevoDesktop = nuevosJars.find { it.fileName.toString().contains("desktop") }
        val nuevoCore    = nuevosJars.find { it.fileName.toString().contains("core") }

        val sb = StringBuilder()
        sb.appendLine("@echo off")
        // Esperar a que el JVM cierre y libere los JARs bloqueados en Windows
        sb.appendLine("timeout /t 5 /nobreak >nul")

        // Copiar nuevo JAR con el nombre exacto del viejo (preserva el hash del app.cfg)
        if (nuevoDesktop != null && viejoDesktop != null) {
            sb.appendLine("copy /y \"$nuevoDesktop\" \"$viejoDesktop\"")
        }
        if (nuevoCore != null && viejoCore != null) {
            sb.appendLine("copy /y \"$nuevoCore\" \"$viejoCore\"")
        }

        // Reiniciar la aplicación
        if (exePath != null) {
            sb.appendLine("start \"\" \"$exePath\"")
        }
        // El script se elimina a sí mismo
        sb.appendLine("del \"%~f0\"")

        Files.writeString(batPath, sb.toString())
        return batPath
    }
}

/** Extrae el valor de un campo string de un JSON plano sin dependencias externas. */
private fun jsonField(json: String, field: String): String? =
    Regex("\"$field\"\\s*:\\s*\"([^\"]*)\"").find(json)?.groupValues?.get(1)
