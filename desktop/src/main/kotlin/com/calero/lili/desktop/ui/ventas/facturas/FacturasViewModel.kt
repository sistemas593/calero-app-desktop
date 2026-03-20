package com.calero.lili.desktop.ui.ventas.facturas

import com.calero.lili.core.comprobantesWs.services.ProcesarDocumentosServiceImpl
import com.calero.lili.core.dtos.Paginator
import com.calero.lili.core.enums.TipoPermiso
import com.calero.lili.core.modVentas.dto.GetListDto
import com.calero.lili.core.modVentas.facturas.VtVentasFacturasServiceImpl
import com.calero.lili.core.modVentas.facturas.dto.FilterListDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/** Convierte 8 dígitos "ddMMyyyy" a "dd/MM/yyyy" para FilterListDto; null si incompleto. */
private fun digitosAFecha(digits: String): String? {
    if (digits.length < 8) return null
    return "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4, 8)}"
}

enum class EstadoFiltroFactura(val label: String, val anulada: Boolean?) {
    TODOS("Todos", null),
    ACTIVAS("Activas", false),
    ANULADAS("Anuladas", true)
}

data class FacturasUiState(
    val isLoading: Boolean = false,
    val facturas: List<GetListDto> = emptyList(),
    val paginator: Paginator? = null,
    val currentPage: Int = 0,
    val pageSize: Int = 15,
    val errorMessage: String? = null,
    // filtros
    val filterSerie: String = "",
    val filterSecuencial: String = "",
    val filterFechaDesde: String = "",
    val filterFechaHasta: String = "",
    val filterEstado: EstadoFiltroFactura = EstadoFiltroFactura.TODOS,
    val filterTercero: String = "",
    // firma electrónica
    val firmaDialogFactura: GetListDto? = null,   // null = cerrado
    val firmaMode: String = "WEB",                // "WEB" o "LOC"
    val firmaPassword: String = "",
    val firmaRutaP12: String = "",
    val firmaRutaLogo: String = "",
    val firmando: Boolean = false,
    val firmaResultado: String? = null,
    val firmaError: String? = null
)

class FacturasViewModel(
    private val service: VtVentasFacturasServiceImpl,
    private val procesarService: ProcesarDocumentosServiceImpl,
    private val idData: Long = 1L,
    private val idEmpresa: Long
) {
    private val _state = MutableStateFlow(FacturasUiState(isLoading = true))
    val state: StateFlow<FacturasUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val USUARIO = "desktop-user"
    }

    init {
        cargar()
    }

    private fun cargar(page: Int = _state.value.currentPage) {
        val s = _state.value
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val filtro = FilterListDto().apply {
                    serie                = s.filterSerie.trim().ifBlank { null }
                    secuencial           = s.filterSecuencial.trim().ifBlank { null }
                    anulada              = s.filterEstado.anulada
                }
                filtro.setTerceroNombre(s.filterTercero.trim().ifBlank { null })
                // Las fechas tienen getter que retorna LocalDate y setter que acepta String —
                // Kotlin no puede crear una propiedad sintética con tipos distintos,
                // por lo que se usan los métodos Java directamente.
                filtro.setFechaEmisionDesde(digitosAFecha(s.filterFechaDesde))
                filtro.setFechaEmisionHasta(digitosAFecha(s.filterFechaHasta))
                val pageable = PageRequest.of(page, s.pageSize, Sort.unsorted())
                val result   = service.findAllPaginate(
                    idData, idEmpresa, filtro, pageable, TipoPermiso.TODAS, USUARIO
                )
                _state.update {
                    it.copy(
                        isLoading   = false,
                        facturas    = result.content ?: emptyList(),
                        paginator   = result.paginator,
                        currentPage = page
                    )
                }
            } catch (e: Exception) {
                val msg = e.message ?: ""
                if (msg.contains("No existen datos", ignoreCase = true)) {
                    _state.update { it.copy(isLoading = false, facturas = emptyList(), paginator = null) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = msg.ifBlank { "Error al cargar facturas" }) }
                }
            }
        }
    }

    fun buscar() {
        _state.update { it.copy(currentPage = 0) }
        cargar(0)
    }

    fun limpiarFiltros() {
        _state.update {
            it.copy(
                filterSerie = "", filterSecuencial = "",
                filterFechaDesde = "", filterFechaHasta = "",
                filterEstado = EstadoFiltroFactura.TODOS,
                filterTercero = "", currentPage = 0
            )
        }
        cargar(0)
    }

    // ── Firma electrónica ─────────────────────────────────────────────────────
    fun abrirDialogoFirma(factura: GetListDto) =
        _state.update { it.copy(firmaDialogFactura = factura, firmaMode = "WEB",
            firmaPassword = "", firmaRutaP12 = "", firmaRutaLogo = "",
            firmaError = null, firmaResultado = null) }

    fun cerrarDialogoFirma() =
        _state.update { it.copy(firmaDialogFactura = null, firmaError = null) }

    fun setFirmaMode(mode: String)      = _state.update { it.copy(firmaMode = mode, firmaError = null) }
    fun setFirmaPassword(v: String)     = _state.update { it.copy(firmaPassword = v) }
    fun setFirmaRutaP12(ruta: String)   = _state.update { it.copy(firmaRutaP12 = ruta) }
    fun setFirmaRutaLogo(ruta: String)  = _state.update { it.copy(firmaRutaLogo = ruta) }
    fun dismissFirmaResultado()         = _state.update { it.copy(firmaResultado = null) }

    fun procesarFirma() {
        val s       = _state.value
        val factura = s.firmaDialogFactura ?: return
        val id      = factura.idVenta ?: return

        if (s.firmaMode == "LOC" && s.firmaRutaP12.isBlank()) {
            _state.update { it.copy(firmaError = "Debe seleccionar el archivo .p12") }
            return
        }
        scope.launch {
            _state.update { it.copy(firmando = true, firmaError = null) }
            try {
                procesarService.procesarFacNcNd(
                    idData,
                    idEmpresa,
                    id,
                    s.firmaMode,
                    s.firmaPassword.ifBlank { null },
                    s.firmaRutaP12.ifBlank { null },
                    s.firmaRutaLogo.ifBlank { null }
                )
                _state.update { it.copy(
                    firmando         = false,
                    firmaDialogFactura = null,
                    firmaResultado   = "Factura firmada y procesada correctamente"
                ) }
                cargar()
            } catch (e: Exception) {
                _state.update { it.copy(firmando = false, firmaError = e.message ?: "Error al procesar la firma") }
            }
        }
    }

    // ── General ───────────────────────────────────────────────────────────────
    fun irPagina(page: Int)                              = cargar(page)
    fun setFilterSerie(v: String)                        = _state.update { it.copy(filterSerie = v) }
    fun setFilterSecuencial(v: String)                   = _state.update { it.copy(filterSecuencial = v) }
    fun setFilterFechaDesde(v: String)                   = _state.update { it.copy(filterFechaDesde = v) }
    fun setFilterFechaHasta(v: String)                   = _state.update { it.copy(filterFechaHasta = v) }
    fun setFilterEstado(v: EstadoFiltroFactura)          = _state.update { it.copy(filterEstado = v) }
    fun setFilterTercero(v: String)                      = _state.update { it.copy(filterTercero = v) }
    fun dismissError()                                   = _state.update { it.copy(errorMessage = null) }
    fun onDestroy()                                      = scope.cancel()
}
