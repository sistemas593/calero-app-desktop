package com.calero.lili.desktop.ui.ventas.facturas

import com.calero.lili.core.comprobantesWs.services.ProcesarDocumentosServiceImpl
import java.util.UUID
import com.calero.lili.core.dtos.Paginator
import com.calero.lili.core.enums.TipoPermiso
import com.calero.lili.core.modTerceros.GeTercerosServiceImpl
import com.calero.lili.core.modTerceros.dto.GeTerceroFilterDto
import com.calero.lili.core.modTerceros.dto.GeTerceroGetListDto
import com.calero.lili.core.modVentas.dto.GetListDto
import com.calero.lili.core.modVentas.facturas.VtVentasFacturasServiceImpl
import com.calero.lili.core.modVentas.facturas.dto.FilterListDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
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
    val filterIdTercero: UUID? = null,
    // live search terceros
    val terceroSugerencias: List<GeTerceroGetListDto> = emptyList(),
    val terceroDropdownVisible: Boolean = false,
    val buscandoTercero: Boolean = false,
    // firma electrónica
    val firmaDialogFactura: GetListDto? = null,   // null = cerrado
    val firmando: Boolean = false,
    val firmaResultado: String? = null,
    val firmaError: String? = null
)

class FacturasViewModel(
    private val service: VtVentasFacturasServiceImpl,
    private val procesarService: ProcesarDocumentosServiceImpl,
    private val tercerosService: GeTercerosServiceImpl,
    private val idData: Long = 1L,
    private val idEmpresa: Long
) {
    private val _state = MutableStateFlow(FacturasUiState(isLoading = true))
    val state: StateFlow<FacturasUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var terceroSearchJob: Job? = null

    companion object {
        private const val USUARIO = "desktop-user"
    }

    init {
        cargar()
    }

    private fun cargar(page: Int = _state.value.currentPage, snapshot: FacturasUiState = _state.value) {
        val s = snapshot
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val filtro = FilterListDto()
                filtro.serie      = s.filterSerie.trim().ifBlank { null }
                filtro.secuencial = s.filterSecuencial.trim().ifBlank { null }
                filtro.anulada    = s.filterEstado.anulada
                // Filtro por tercero: si el usuario seleccionó del dropdown se usa el UUID exacto,
                // si escribió manualmente se usa el nombre con LIKE (terceroNombre).
                s.filterIdTercero?.let { filtro.idTercero = it }
                if (s.filterIdTercero == null) {
                    filtro.setTerceroNombre(s.filterTercero.trim().ifBlank { null })
                }
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
        // Capturar el snapshot del estado ANTES de cualquier actualización para
        // garantizar que filterIdTercero llegue íntegro a cargar(), sin importar
        // eventos de UI que puedan llegar entre el update y la coroutine.
        val snapshot = _state.value.copy(currentPage = 0)
        _state.update { it.copy(currentPage = 0) }
        cargar(0, snapshot)
    }

    fun limpiarFiltros() {
        _state.update {
            it.copy(
                filterSerie = "", filterSecuencial = "",
                filterFechaDesde = "", filterFechaHasta = "",
                filterEstado = EstadoFiltroFactura.TODOS,
                filterTercero = "", filterIdTercero = null, currentPage = 0
            )
        }
        cargar(0)
    }

    // ── Firma electrónica ─────────────────────────────────────────────────────
    fun abrirDialogoFirma(factura: GetListDto) =
        _state.update { it.copy(firmaDialogFactura = factura,
            firmaError = null, firmaResultado = null) }

    fun cerrarDialogoFirma() =
        _state.update { it.copy(firmaDialogFactura = null, firmaError = null) }

    fun dismissFirmaResultado() = _state.update { it.copy(firmaResultado = null) }

    fun procesarFirma() {
        val s       = _state.value
        val factura = s.firmaDialogFactura ?: return
        val id      = factura.idVenta ?: return
        scope.launch {
            _state.update { it.copy(firmando = true, firmaError = null) }
            try {
                procesarService.procesarFacNcNd(
                    idData,
                    idEmpresa,
                    id,
                    "LOC"
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
    fun setFilterTercero(v: String) {
        _state.update { current ->
            // Si el nuevo valor es idéntico al texto que ya mostramos Y tenemos un tercero
            // seleccionado, es un evento de refocus del campo (no input real del usuario):
            // preservamos filterIdTercero para que buscar() lo use correctamente.
            val preservarId = current.filterIdTercero != null && v == current.filterTercero
            current.copy(
                filterTercero          = v,
                filterIdTercero        = if (preservarId) current.filterIdTercero else null,
                terceroDropdownVisible = false,
                terceroSugerencias     = emptyList()
            )
        }
        terceroSearchJob?.cancel()
        // Solo lanzar búsqueda si NO hay tercero seleccionado y el texto es suficiente
        val st = _state.value
        if (st.filterIdTercero == null && v.trim().length >= 2) {
            terceroSearchJob = scope.launch {
                delay(300)
                buscarTercerosSugerencias(v.trim())
            }
        }
    }

    private fun buscarTercerosSugerencias(query: String) {
        scope.launch {
            _state.update { it.copy(buscandoTercero = true) }
            try {
                val filterDto = GeTerceroFilterDto().apply { filter = query }
                val pageable  = PageRequest.of(0, 10, Sort.unsorted())
                val result    = tercerosService.findAllPaginate(idData, filterDto, pageable)
                val lista     = result.content ?: emptyList()
                _state.update {
                    it.copy(
                        buscandoTercero        = false,
                        terceroSugerencias     = lista,
                        terceroDropdownVisible = lista.isNotEmpty()
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(buscandoTercero = false, terceroSugerencias = emptyList(), terceroDropdownVisible = false) }
            }
        }
    }

    fun seleccionarTercero(tercero: GeTerceroGetListDto) {
        _state.update {
            it.copy(
                filterTercero          = tercero.tercero ?: "",
                filterIdTercero        = tercero.idTercero,
                terceroSugerencias     = emptyList(),
                terceroDropdownVisible = false
            )
        }
    }

    fun cerrarDropdownTercero() =
        _state.update { it.copy(terceroDropdownVisible = false) }

    fun dismissError()                                   = _state.update { it.copy(errorMessage = null) }
    fun onDestroy()                                      = scope.cancel()
}
