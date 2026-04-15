package com.calero.lili.desktop.ui.ventas.adlogs

import com.calero.lili.core.adLogs.AdLogsServiceImpl
import com.calero.lili.core.adLogs.dto.AdLogsDtoResponse
import com.calero.lili.core.dtos.FilterFechasDto
import com.calero.lili.core.dtos.Paginator
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

/** Convierte 8 dígitos "ddMMyyyy" a "dd/MM/yyyy"; null si incompleto. */
private fun digitosAFecha(digits: String): String? {
    if (digits.length < 8) return null
    return "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4, 8)}"
}

enum class TipoFiltroLog(val label: String, val valor: String?) {
    TODOS("Todos", null),
    INFORMATIVO("Informativo", "I"),
    ERROR("Error", "E")
}

data class AdLogsUiState(
    val isLoading: Boolean = false,
    val logs: List<AdLogsDtoResponse> = emptyList(),
    val paginator: Paginator? = null,
    val currentPage: Int = 0,
    val pageSize: Int = 10,
    val errorMessage: String? = null,
    val filterFechaDesde: String = "",
    val filterFechaHasta: String = "",
    val filterTipo: TipoFiltroLog = TipoFiltroLog.TODOS
)

class AdLogsViewModel(
    private val service: AdLogsServiceImpl,
    private val idData: Long,
    private val idEmpresa: Long
) {
    private val _state = MutableStateFlow(AdLogsUiState())
    val state: StateFlow<AdLogsUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // ── Filtros ───────────────────────────────────────────────────────────────
    fun setFilterFechaDesde(v: String) =
        _state.update { it.copy(filterFechaDesde = v.filter(Char::isDigit).take(8)) }

    fun setFilterFechaHasta(v: String) =
        _state.update { it.copy(filterFechaHasta = v.filter(Char::isDigit).take(8)) }

    fun setFilterTipo(v: TipoFiltroLog) =
        _state.update { it.copy(filterTipo = v) }

    // ── Acciones ──────────────────────────────────────────────────────────────
    /** Captura el snapshot actual y arranca en la página 0. */
    fun buscar() = cargar(page = 0, snapshot = _state.value)

    /** Navega a otra página reutilizando el mismo snapshot. */
    fun irPagina(page: Int) = cargar(page = page, snapshot = _state.value)

    fun limpiarFiltros() = _state.update { AdLogsUiState() }

    fun dismissError() = _state.update { it.copy(errorMessage = null) }

    fun onDestroy() = scope.cancel()

    // ── Carga ─────────────────────────────────────────────────────────────────
    private fun cargar(page: Int, snapshot: AdLogsUiState) {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val filtro = FilterFechasDto()
                filtro.setFechaDesde(digitosAFecha(snapshot.filterFechaDesde))
                filtro.setFechaHasta(digitosAFecha(snapshot.filterFechaHasta))
                filtro.setTipo(snapshot.filterTipo.valor)

                val pageable = PageRequest.of(page, snapshot.pageSize, Sort.unsorted())
                val result = service.findAllPageable(idData, idEmpresa, filtro, pageable)

                _state.update {
                    it.copy(
                        isLoading   = false,
                        logs        = result.content ?: emptyList(),
                        paginator   = result.paginator,
                        currentPage = page
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar los registros")
                }
            }
        }
    }
}
