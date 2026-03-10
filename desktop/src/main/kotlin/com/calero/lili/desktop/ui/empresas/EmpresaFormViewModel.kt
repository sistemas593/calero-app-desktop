package com.calero.lili.desktop.ui.empresas

import com.calero.lili.core.enums.TipoContribuyente
import com.calero.lili.core.modAdminEmpresas.AdEmpresasServiceImpl
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaRequestDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmpresaFormUiState(
    // ── Sección 1: Datos Generales
    val razonSocial: String = "",
    val ruc: String = "",
    val email: String = "",
    val ciudad: String = "",
    val telefono1: String = "",
    val telefono2: String = "",
    val direccionMatriz: String = "",
    val numero: String = "",

    // ── Sección 2: Información Fiscal
    val tipoContribuyente: TipoContribuyente? = null,
    val obligadoContabilidad: String = "N",
    val devolucionIva: String = "N",
    val agenteRetencion: String = "N",
    val contribuyenteEspecial: String = "",
    val formaPagoSri: String = "",
    val codigoSustento: String = "",

    // ── Sección 3: Contador y Representante
    val contadorNombre: String = "",
    val contadorRuc: String = "",
    val representanteNombre: String = "",
    val representanteTipoIdentificacion: String = "",
    val representanteIdentificacion: String = "",

    // ── Sección 4: Firma Digital y Momentos de Envío
    val contraseniaFirma: String = "",
    val fechaCaducidadCertificado: String = "",
    val momentoEnvioFactura: String = "2",
    val momentoEnvioNotaCredito: String = "2",
    val momentoEnvioNotaDebito: String = "2",
    val momentoEnvioGuiaRemision: String = "2",
    val momentoEnvioLiquidacion: String = "2",
    val momentoEnvioComprobanteRetencion: String = "2",

    // ── Control de UI
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isEditMode: Boolean = false
)

class EmpresaFormViewModel(
    private val service: AdEmpresasServiceImpl,
    private val idEmpresa: Long? = null
) {

    private val _state = MutableStateFlow(EmpresaFormUiState(isEditMode = idEmpresa != null))
    val state: StateFlow<EmpresaFormUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val ID_DATA = 1L
        private const val USUARIO = "desktop-user"
    }

    init {
        if (idEmpresa != null) {
            cargarEmpresa(idEmpresa)
        }
    }

    // ── Carga datos de empresa existente para modo edición
    private fun cargarEmpresa(id: Long) {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val dto = service.findById(ID_DATA, id)
                _state.update { current ->
                    current.copy(
                        isLoading = false,
                        razonSocial = dto.razonSocial ?: "",
                        ruc = dto.ruc ?: "",
                        email = dto.email ?: "",
                        ciudad = dto.ciudad ?: "",
                        telefono1 = dto.telefono1 ?: "",
                        telefono2 = dto.telefono2 ?: "",
                        direccionMatriz = dto.direccionMatriz ?: "",
                        numero = dto.numero ?: "",
                        tipoContribuyente = dto.tipoContribuyente,
                        obligadoContabilidad = dto.obligadoContabilidad ?: "N",
                        devolucionIva = dto.devolucionIva ?: "N",
                        agenteRetencion = dto.agenteRetencion ?: "N",
                        contribuyenteEspecial = dto.contribuyenteEspecial ?: "",
                        formaPagoSri = dto.formaPagoSri ?: "",
                        codigoSustento = dto.codigoSustento ?: "",
                        contadorNombre = dto.contadorNombre ?: "",
                        contadorRuc = dto.contadorRuc ?: "",
                        representanteNombre = dto.representanteNombre ?: "",
                        // ⚠️ AdEmpresaGetOneDto tiene typo: representanteTipoIdenficacion (falta segunda "i")
                        representanteTipoIdentificacion = dto.representanteTipoIdenficacion ?: "",
                        representanteIdentificacion = dto.representanteIdentificacion ?: "",
                        contraseniaFirma = "", // nunca pre-rellenar contraseñas
                        fechaCaducidadCertificado = dto.fechaCaducidadCertificado ?: "",
                        momentoEnvioFactura = dto.momentoEnvioFactura.toString(),
                        momentoEnvioNotaCredito = dto.momentoEnvioNotaCredito.toString(),
                        momentoEnvioNotaDebito = dto.momentoEnvioNotaDebito.toString(),
                        momentoEnvioGuiaRemision = dto.momentoEnvioGuiaRemision.toString(),
                        momentoEnvioLiquidacion = dto.momentoEnvioLiquidacion.toString(),
                        momentoEnvioComprobanteRetencion = dto.momentoEnvioComprobanteRetencion.toString()
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, errorMessage = "Error al cargar empresa: ${e.message}")
                }
            }
        }
    }

    // ── Guarda la empresa (crear o actualizar)
    fun guardar() {
        val current = _state.value

        // Validaciones client-side
        if (current.razonSocial.isBlank()) {
            _state.update { it.copy(errorMessage = "La razón social es requerida") }
            return
        }
        if (current.ruc.isBlank()) {
            _state.update { it.copy(errorMessage = "El RUC es requerido") }
            return
        }
        if (current.tipoContribuyente == null) {
            _state.update { it.copy(errorMessage = "El tipo de contribuyente es requerido") }
            return
        }
        if (current.direccionMatriz.isBlank()) {
            _state.update { it.copy(errorMessage = "La dirección matriz es requerida") }
            return
        }

        val request = AdEmpresaRequestDto.builder()
            .razonSocial(current.razonSocial.trim())
            .ruc(current.ruc.trim())
            .email(current.email.trim())
            .ciudad(current.ciudad.trim())
            .telefono1(current.telefono1.trim())
            .telefono2(current.telefono2.trim())
            .direccionMatriz(current.direccionMatriz.trim())
            .numero(current.numero.trim())
            .tipoContribuyente(current.tipoContribuyente)
            .obligadoContabilidad(current.obligadoContabilidad)
            .devolucionIva(current.devolucionIva)
            .agenteRetencion(current.agenteRetencion)
            .contribuyenteEspecial(current.contribuyenteEspecial.trim())
            .formaPagoSri(current.formaPagoSri.trim())
            .codigoSustento(current.codigoSustento.trim())
            .contadorNombre(current.contadorNombre.trim())
            .contadorRuc(current.contadorRuc.trim())
            .representanteNombre(current.representanteNombre.trim())
            .representanteTipoIdentificacion(current.representanteTipoIdentificacion.trim())
            .representanteIdentificacion(current.representanteIdentificacion.trim())
            .contraseniaFirma(current.contraseniaFirma.ifBlank { null })
            .fechaCaducidadCertificado(current.fechaCaducidadCertificado.ifBlank { null })
            .momentoEnvioFactura(current.momentoEnvioFactura.toIntOrNull() ?: 2)
            .momentoEnvioNotaCredito(current.momentoEnvioNotaCredito.toIntOrNull() ?: 2)
            .momentoEnvioNotaDebito(current.momentoEnvioNotaDebito.toIntOrNull() ?: 2)
            .momentoEnvioGuiaRemision(current.momentoEnvioGuiaRemision.toIntOrNull() ?: 2)
            .momentoEnvioLiquidacion(current.momentoEnvioLiquidacion.toIntOrNull() ?: 2)
            .momentoEnvioComprobanteRetencion(current.momentoEnvioComprobanteRetencion.toIntOrNull() ?: 2)
            .build()

        scope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            try {
                if (idEmpresa != null) {
                    service.update(ID_DATA, idEmpresa, request, USUARIO)
                    _state.update { it.copy(isSaving = false, successMessage = "Empresa actualizada correctamente") }
                } else {
                    service.create(ID_DATA, request, USUARIO)
                    _state.update { it.copy(isSaving = false, successMessage = "Empresa creada correctamente") }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isSaving = false, errorMessage = e.message ?: "Error al guardar la empresa")
                }
            }
        }
    }

    // ── Setters individuales por campo
    fun setRazonSocial(v: String) = _state.update { it.copy(razonSocial = v, errorMessage = null) }
    fun setRuc(v: String) = _state.update { it.copy(ruc = v, errorMessage = null) }
    fun setEmail(v: String) = _state.update { it.copy(email = v) }
    fun setCiudad(v: String) = _state.update { it.copy(ciudad = v) }
    fun setTelefono1(v: String) = _state.update { it.copy(telefono1 = v) }
    fun setTelefono2(v: String) = _state.update { it.copy(telefono2 = v) }
    fun setDireccionMatriz(v: String) = _state.update { it.copy(direccionMatriz = v, errorMessage = null) }
    fun setNumero(v: String) = _state.update { it.copy(numero = v) }
    fun setTipoContribuyente(v: TipoContribuyente?) = _state.update { it.copy(tipoContribuyente = v, errorMessage = null) }
    fun setObligadoContabilidad(v: String) = _state.update { it.copy(obligadoContabilidad = v) }
    fun setDevolucionIva(v: String) = _state.update { it.copy(devolucionIva = v) }
    fun setAgenteRetencion(v: String) = _state.update { it.copy(agenteRetencion = v) }
    fun setContribuyenteEspecial(v: String) = _state.update { it.copy(contribuyenteEspecial = v) }
    fun setFormaPagoSri(v: String) = _state.update { it.copy(formaPagoSri = v) }
    fun setCodigoSustento(v: String) = _state.update { it.copy(codigoSustento = v) }
    fun setContadorNombre(v: String) = _state.update { it.copy(contadorNombre = v) }
    fun setContadorRuc(v: String) = _state.update { it.copy(contadorRuc = v) }
    fun setRepresentanteNombre(v: String) = _state.update { it.copy(representanteNombre = v) }
    fun setRepresentanteTipoIdentificacion(v: String) = _state.update { it.copy(representanteTipoIdentificacion = v) }
    fun setRepresentanteIdentificacion(v: String) = _state.update { it.copy(representanteIdentificacion = v) }
    fun setContraseniaFirma(v: String) = _state.update { it.copy(contraseniaFirma = v) }
    fun setFechaCaducidadCertificado(v: String) = _state.update { it.copy(fechaCaducidadCertificado = v) }
    fun setMomentoEnvioFactura(v: String) = _state.update { it.copy(momentoEnvioFactura = v) }
    fun setMomentoEnvioNotaCredito(v: String) = _state.update { it.copy(momentoEnvioNotaCredito = v) }
    fun setMomentoEnvioNotaDebito(v: String) = _state.update { it.copy(momentoEnvioNotaDebito = v) }
    fun setMomentoEnvioGuiaRemision(v: String) = _state.update { it.copy(momentoEnvioGuiaRemision = v) }
    fun setMomentoEnvioLiquidacion(v: String) = _state.update { it.copy(momentoEnvioLiquidacion = v) }
    fun setMomentoEnvioComprobanteRetencion(v: String) = _state.update { it.copy(momentoEnvioComprobanteRetencion = v) }

    fun onDestroy() {
        scope.cancel()
    }
}
