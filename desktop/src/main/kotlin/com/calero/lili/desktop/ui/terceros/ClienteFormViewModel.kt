package com.calero.lili.desktop.ui.terceros

import com.calero.lili.core.enums.TipoIdentificacion
import com.calero.lili.core.modTerceros.GeTercerosServiceImpl
import com.calero.lili.core.modTerceros.dto.GeTerceroRequestDto
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

data class ClienteFormUiState(
    // Datos generales
    val tercero: String = "",
    val tipoIdentificacion: TipoIdentificacion? = null,
    val numeroIdentificacion: String = "",
    val ciudad: String = "",
    val direccion: String = "",
    val telefonos: String = "",
    val contacto: String = "",
    val email: String = "",
    val web: String = "",
    val observaciones: String = "",
    val tipoClienteProveedor: String = "",
    // Tipos de tercero
    val esCliente: Boolean = true,
    val esProveedor: Boolean = false,
    val esTransportista: Boolean = false,
    val placa: String = "",
    val esTrabajador: Boolean = false,
    // UI
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isEditMode: Boolean = false
)

class ClienteFormViewModel(
    private val service: GeTercerosServiceImpl,
    private val idCliente: UUID? = null,
    private val idData: Long = 1L,
    private val idEmpresa: Long = 1L
) {
    private val _state = MutableStateFlow(ClienteFormUiState(isEditMode = idCliente != null))
    val state: StateFlow<ClienteFormUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val USUARIO = "desktop-user"
    }

    init {
        if (idCliente != null) cargarCliente(idCliente)
    }

    private fun cargarCliente(id: UUID) {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val dto = service.findById(idData, id, idEmpresa)
                _state.update { current ->
                    current.copy(
                        isLoading            = false,
                        tercero              = dto.tercero ?: "",
                        tipoIdentificacion   = parseTipoIdentificacion(dto.tipoIdentificacion),
                        numeroIdentificacion = dto.numeroIdentificacion ?: "",
                        ciudad               = dto.ciudad ?: "",
                        direccion            = dto.direccion ?: "",
                        telefonos            = dto.telefonos ?: "",
                        contacto             = dto.contacto ?: "",
                        email                = dto.email ?: "",
                        web                  = dto.web ?: "",
                        observaciones        = dto.observaciones ?: "",
                        tipoClienteProveedor = dto.tipoClienteProveedor ?: "",
                        esCliente            = dto.cliente?.esCliente ?: true,
                        esProveedor          = dto.proveedor?.esProveedor ?: false,
                        esTransportista      = dto.transportista?.esTransportista ?: false,
                        placa                = dto.transportista?.placa ?: "",
                        esTrabajador         = dto.trabajador?.esTrabajador ?: false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Error al cargar cliente: ${e.message}") }
            }
        }
    }

    fun guardar() {
        val current = _state.value
        if (current.tercero.isBlank()) {
            _state.update { it.copy(errorMessage = "El nombre del cliente es requerido") }
            return
        }
        if (current.esTransportista && current.placa.isBlank()) {
            _state.update { it.copy(errorMessage = "La placa es requerida cuando es transportista") }
            return
        }

        val request = GeTerceroRequestDto.builder()
            .tercero(current.tercero.trim())
            .tipoIdentificacion(current.tipoIdentificacion)
            .numeroIdentificacion(current.numeroIdentificacion.trim().ifBlank { null })
            .ciudad(current.ciudad.trim().ifBlank { null })
            .direccion(current.direccion.trim().ifBlank { null })
            .telefonos(current.telefonos.trim().ifBlank { null })
            .contacto(current.contacto.trim().ifBlank { null })
            .email(current.email.trim().ifBlank { null })
            .web(current.web.trim().ifBlank { null })
            .observaciones(current.observaciones.trim().ifBlank { null })
            .tipoClienteProveedor(current.tipoClienteProveedor.trim().ifBlank { null })
            .cliente(GeTerceroRequestDto.TipoTercerosClienteDto(true, null))
            .proveedor(GeTerceroRequestDto.TipoTercerosProveedorDto(false, null))
            .transportista(GeTerceroRequestDto.TipoTercerosTransportistaDto(false, null))
            .trabajador(GeTerceroRequestDto.TipoTerceroTrabajador(false, null))
            .build()

        scope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            try {
                if (idCliente != null) {
                    service.update(idEmpresa, idData, idCliente, request, USUARIO)
                    _state.update { it.copy(isSaving = false, successMessage = "Cliente actualizado correctamente") }
                } else {
                    service.create(idEmpresa, idData, request, USUARIO)
                    _state.update { it.copy(isSaving = false, successMessage = "Cliente creado correctamente") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, errorMessage = e.message ?: "Error al guardar el cliente") }
            }
        }
    }

    // ── Setters
    fun setTercero(v: String)              = _state.update { it.copy(tercero = v, errorMessage = null) }
    fun setTipoIdentificacion(v: TipoIdentificacion?) = _state.update { it.copy(tipoIdentificacion = v) }
    fun setNumeroIdentificacion(v: String) = _state.update { it.copy(numeroIdentificacion = v) }
    fun setCiudad(v: String)               = _state.update { it.copy(ciudad = v) }
    fun setDireccion(v: String)            = _state.update { it.copy(direccion = v) }
    fun setTelefonos(v: String)            = _state.update { it.copy(telefonos = v) }
    fun setContacto(v: String)             = _state.update { it.copy(contacto = v) }
    fun setEmail(v: String)                = _state.update { it.copy(email = v) }
    fun setWeb(v: String)                  = _state.update { it.copy(web = v) }
    fun setObservaciones(v: String)        = _state.update { it.copy(observaciones = v) }
    fun setTipoClienteProveedor(v: String) = _state.update { it.copy(tipoClienteProveedor = v) }
    fun setEsCliente(v: Boolean)           = _state.update { it.copy(esCliente = v) }
    fun setEsProveedor(v: Boolean)         = _state.update { it.copy(esProveedor = v) }
    fun setEsTransportista(v: Boolean)     = _state.update { it.copy(esTransportista = v, placa = if (!v) "" else it.placa) }
    fun setPlaca(v: String)                = _state.update { it.copy(placa = v) }
    fun setEsTrabajador(v: Boolean)        = _state.update { it.copy(esTrabajador = v) }
    fun dismissError()                     = _state.update { it.copy(errorMessage = null) }
    fun onDestroy()                        = scope.cancel()

    private fun parseTipoIdentificacion(valor: String?): TipoIdentificacion? {
        if (valor.isNullOrBlank()) return null
        return try { TipoIdentificacion.valueOf(valor) } catch (_: Exception) { null }
    }
}
