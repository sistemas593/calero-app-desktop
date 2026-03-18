package com.calero.lili.desktop.ui.empresas.series

import com.calero.lili.core.dtos.Paginator
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesServiceImpl
import com.calero.lili.core.modAdminEmpresasSeries.dto.AdEmpresaSerieGetListDto
import com.calero.lili.core.modAdminEmpresasSeries.dto.AdEmpresaSerieListFilterDto
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
import java.util.UUID

data class SeriesUiState(
    val isLoading: Boolean = false,
    val series: List<AdEmpresaSerieGetListDto> = emptyList(),
    val paginator: Paginator? = null,
    val filterText: String = "",
    val currentPage: Int = 0,
    val pageSize: Int = 10,
    val errorMessage: String? = null,
    val showForm: Boolean = false,
    val editingId: UUID? = null
)

class SeriesViewModel(
    private val service: AdEmpresasSeriesServiceImpl,
    private val idData: Long = 1L,
    private val idEmpresa: Long = 1L
) {

    private val _state = MutableStateFlow(SeriesUiState())
    val state: StateFlow<SeriesUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        cargar()
    }

    fun setFilter(text: String) {
        _state.update { it.copy(filterText = text) }
    }

    fun buscar() {
        _state.update { it.copy(currentPage = 0) }
        cargar()
    }

    fun cargar() {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val current = _state.value
                val filterDto = AdEmpresaSerieListFilterDto().apply {
                    filter = current.filterText.ifBlank { null }
                }
                val pageable = PageRequest.of(current.currentPage, current.pageSize, Sort.unsorted())
                val result = service.findAllPaginate(idData, idEmpresa, filterDto, pageable)
                _state.update {
                    it.copy(
                        isLoading = false,
                        series = result.content ?: emptyList(),
                        paginator = result.paginator
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Error desconocido")
                }
            }
        }
    }

    fun paginaAnterior() {
        val current = _state.value.currentPage
        if (current > 0) {
            _state.update { it.copy(currentPage = current - 1) }
            cargar()
        }
    }

    fun paginaSiguiente() {
        val paginator = _state.value.paginator ?: return
        val current = _state.value.currentPage
        if (current < paginator.totalPages - 1) {
            _state.update { it.copy(currentPage = current + 1) }
            cargar()
        }
    }

    fun abrirFormulario(idSerie: UUID? = null) {
        _state.update { it.copy(showForm = true, editingId = idSerie) }
    }

    fun cerrarFormulario() {
        _state.update { it.copy(showForm = false, editingId = null) }
        cargar()
    }

    fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun onDestroy() {
        scope.cancel()
    }
}
