package com.calero.lili.desktop.ui.empresas.series

import com.calero.lili.core.enums.FormatoDocumento
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesServiceImpl
import com.calero.lili.core.modAdminEmpresasSeries.dto.AdEmpresaSerieCreationRequestDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class DocumentoFormState(
    val documento: String = "",
    val numeroAutorizacion: String = "",
    val secuencial: String = "",
    val formatoDocumento: FormatoDocumento? = null,
    val desde: String = "",
    val hasta: String = "",
    val fechaVencimiento: String = ""
)

data class SerieFormUiState(
    val serie: String = "",
    val nombreComercial: String = "",
    val direccionEstablecimiento: String = "",
    val ciudad: String = "",
    val telefono1: String = "",
    val telefono2: String = "",
    val documentos: List<DocumentoFormState> = listOf(DocumentoFormState(documento = "FAC")),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isEditMode: Boolean = false
)

class SerieFormViewModel(
    private val service: AdEmpresasSeriesServiceImpl,
    private val idSerie: UUID? = null,
    private val idData: Long = 1L,
    private val idEmpresa: Long = 1L
) {

    private val _state = MutableStateFlow(SerieFormUiState(isEditMode = idSerie != null))
    val state: StateFlow<SerieFormUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val USUARIO = "desktop-user"
    }

    init {
        if (idSerie != null) {
            cargarSerie(idSerie)
        }
    }

    private fun cargarSerie(id: UUID) {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val dto = service.findById(idData, idEmpresa, id)
                val documentosCargados = dto.documentos?.map { doc ->
                    DocumentoFormState(
                        documento          = doc.documento ?: "",
                        numeroAutorizacion = doc.numeroAutorizacion ?: "",
                        secuencial         = doc.secuencial ?: "",
                        formatoDocumento   = doc.formatoDocumento,
                        desde              = doc.desde ?: "",
                        hasta              = doc.hasta ?: "",
                        fechaVencimiento   = doc.fechaVencimiento ?: ""
                    )
                } ?: emptyList()
                _state.update { current ->
                    current.copy(
                        isLoading                = false,
                        serie                    = dto.serie ?: "",
                        nombreComercial          = dto.nombreComercial ?: "",
                        direccionEstablecimiento = dto.direccionEstablecimiento ?: "",
                        ciudad                   = dto.ciudad ?: "",
                        telefono1                = dto.telefono1 ?: "",
                        telefono2                = dto.telefono2 ?: "",
                        documentos               = documentosCargados
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, errorMessage = "Error al cargar serie: ${e.message}")
                }
            }
        }
    }

    fun guardar() {
        val current = _state.value
        if (current.serie.isBlank()) {
            _state.update { it.copy(errorMessage = "La serie es requerida") }
            return
        }
        if (current.nombreComercial.isBlank()) {
            _state.update { it.copy(errorMessage = "El nombre comercial es requerido") }
            return
        }
        // Validar que cada documento tenga formatoDocumento
        val docInvalido = current.documentos.indexOfFirst { it.formatoDocumento == null }
        if (docInvalido >= 0) {
            _state.update { it.copy(errorMessage = "El documento #${docInvalido + 1} debe tener un formato seleccionado") }
            return
        }

        val docList = current.documentos.map { doc ->
            AdEmpresaSerieCreationRequestDto.Documentos.builder()
                .documento(doc.documento.trim())
                .numeroAutorizacion(doc.numeroAutorizacion.trim())
                .secuencial(doc.secuencial.trim())
                .formatoDocumento(doc.formatoDocumento)
                .desde(doc.desde.trim())
                .hasta(doc.hasta.trim())
                .fechaVencimiento(doc.fechaVencimiento.trim().ifBlank { null })
                .build()
        }

        val request = AdEmpresaSerieCreationRequestDto.builder()
            .serie(current.serie.trim())
            .nombreComercial(current.nombreComercial.trim())
            .direccionEstablecimiento(current.direccionEstablecimiento.trim())
            .ciudad(current.ciudad.trim())
            .telefono1(current.telefono1.trim())
            .telefono2(current.telefono2.trim())
            .documentos(docList)
            .build()

        scope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            try {
                if (idSerie != null) {
                    service.update(idData, idEmpresa, idSerie, request, USUARIO)
                    _state.update { it.copy(isSaving = false, successMessage = "Serie actualizada correctamente") }
                } else {
                    service.create(idData, idEmpresa, request, USUARIO)
                    _state.update { it.copy(isSaving = false, successMessage = "Serie creada correctamente") }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isSaving = false, errorMessage = e.message ?: "Error al guardar la serie")
                }
            }
        }
    }

    // ── Setters de campos principales
    fun setSerie(v: String)                    = _state.update { it.copy(serie = v, errorMessage = null) }
    fun setNombreComercial(v: String)          = _state.update { it.copy(nombreComercial = v, errorMessage = null) }
    fun setDireccionEstablecimiento(v: String) = _state.update { it.copy(direccionEstablecimiento = v) }
    fun setCiudad(v: String)                   = _state.update { it.copy(ciudad = v) }
    fun setTelefono1(v: String)                = _state.update { it.copy(telefono1 = v) }
    fun setTelefono2(v: String)                = _state.update { it.copy(telefono2 = v) }

    // ── Gestión de documentos
    fun agregarDocumento() {
        _state.update { it.copy(documentos = it.documentos + DocumentoFormState()) }
    }

    fun eliminarDocumento(index: Int) {
        _state.update { it.copy(documentos = it.documentos.toMutableList().also { list -> list.removeAt(index) }) }
    }

    fun updateDocumento(index: Int, doc: DocumentoFormState) {
        _state.update {
            it.copy(documentos = it.documentos.toMutableList().also { list -> list[index] = doc })
        }
    }

    fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun onDestroy() {
        scope.cancel()
    }
}
