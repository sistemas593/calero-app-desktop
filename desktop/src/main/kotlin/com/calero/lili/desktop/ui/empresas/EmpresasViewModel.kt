package com.calero.lili.desktop.ui.empresas

import com.calero.lili.core.dtos.Paginator
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaGetListDto
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaListFilterDto
import com.calero.lili.core.modAdminEmpresas.AdEmpresasServiceImpl
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

data class EmpresasUiState(
    val isLoading: Boolean = false,
    val empresas: List<AdEmpresaGetListDto> = emptyList(),
    val paginator: Paginator? = null,
    val filterText: String = "",
    val estadoFiltro: Int? = null,
    val currentPage: Int = 0,
    val pageSize: Int = 10,
    val errorMessage: String? = null,
    val showForm: Boolean = false,
    val editingId: Long? = null
)

class EmpresasViewModel(private val service: AdEmpresasServiceImpl) {

    private val _state = MutableStateFlow(EmpresasUiState())
    val state: StateFlow<EmpresasUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        cargar()
    }

    fun setFilter(text: String) {
        _state.update { it.copy(filterText = text) }
    }

    fun setEstado(estado: Int?) {
        _state.update { it.copy(estadoFiltro = estado) }
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
                val filterDto = AdEmpresaListFilterDto().apply {
                    filter = current.filterText.ifBlank { null }
                    estado = current.estadoFiltro
                }
                val pageable = PageRequest.of(current.currentPage, current.pageSize, Sort.unsorted())
                val result = service.findAllPaginate(1L, filterDto, pageable)
                _state.update {
                    it.copy(
                        isLoading = false,
                        empresas = result.content ?: emptyList(),
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

    fun irAPagina(pagina: Int) {
        _state.update { it.copy(currentPage = pagina) }
        cargar()
    }

    fun abrirFormulario(idEmpresa: Long? = null) {
        _state.update { it.copy(showForm = true, editingId = idEmpresa) }
    }

    fun cerrarFormulario() {
        _state.update { it.copy(showForm = false, editingId = null) }
        cargar()
    }

    fun onDestroy() {
        scope.cancel()
    }
}
