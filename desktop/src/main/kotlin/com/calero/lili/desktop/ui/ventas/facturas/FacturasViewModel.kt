package com.calero.lili.desktop.ui.ventas.facturas

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

data class FacturasUiState(
    val isLoading: Boolean = false,
    val facturas: List<GetListDto> = emptyList(),
    val paginator: Paginator? = null,
    val currentPage: Int = 0,
    val pageSize: Int = 15,
    val filterText: String = "",
    val errorMessage: String? = null
)

class FacturasViewModel(
    private val service: VtVentasFacturasServiceImpl,
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

    fun cargar(page: Int = 0) {
        val current = _state.value
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            println("[FacturasViewModel] cargar → idData=$idData  idEmpresa=$idEmpresa  page=$page")
            try {
                val filter   = FilterListDto()
                val pageable = PageRequest.of(page, current.pageSize, Sort.unsorted())
                val result   = service.findAllPaginate(
                    idData,
                    idEmpresa,
                    filter,
                    pageable,
                    TipoPermiso.TODAS,
                    USUARIO
                )
                println("[FacturasViewModel] resultado → ${result.paginator?.totalElements} registro(s)")
                val pag = result.paginator
                _state.update {
                    it.copy(
                        isLoading   = false,
                        facturas    = result.content ?: emptyList(),
                        paginator   = pag,
                        currentPage = page
                    )
                }
            } catch (e: Exception) {
                val msg = e.message ?: ""
                println("[FacturasViewModel] excepción → $msg")
                e.printStackTrace()
                if (msg.contains("No existen datos", ignoreCase = true)) {
                    // El servicio lanza excepción cuando no hay registros — se muestra lista vacía
                    _state.update { it.copy(isLoading = false, facturas = emptyList(), paginator = null,
                        errorMessage = "Sin resultados para idData=$idData / idEmpresa=$idEmpresa") }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = msg.ifBlank { "Error al cargar facturas" }) }
                }
            }
        }
    }

    fun irPagina(page: Int)  = cargar(page)
    fun dismissError()       = _state.update { it.copy(errorMessage = null) }
    fun onDestroy()          = scope.cancel()
}
