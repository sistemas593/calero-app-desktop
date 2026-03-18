package com.calero.lili.desktop.ui.terceros

import com.calero.lili.core.dtos.Paginator
import com.calero.lili.core.modTerceros.GeTercerosServiceImpl
import com.calero.lili.core.modTerceros.dto.GeTerceroFilterDto
import com.calero.lili.core.modTerceros.dto.GeTerceroGetListDto
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

data class ClientesUiState(
    val isLoading: Boolean = false,
    val clientes: List<GeTerceroGetListDto> = emptyList(),
    val paginator: Paginator? = null,
    val filterText: String = "",
    val currentPage: Int = 0,
    val pageSize: Int = 10,
    val errorMessage: String? = null,
    val showForm: Boolean = false,
    val editingId: UUID? = null
)

class ClientesViewModel(
    private val service: GeTercerosServiceImpl,
    private val idData: Long = 1L,
    private val idEmpresa: Long = 1L
) {
    private val _state = MutableStateFlow(ClientesUiState())
    val state: StateFlow<ClientesUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init { cargar() }

    fun setFilter(text: String) = _state.update { it.copy(filterText = text) }

    fun buscar() {
        _state.update { it.copy(currentPage = 0) }
        cargar()
    }

    fun cargar() {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val current = _state.value
                val filterDto = GeTerceroFilterDto().apply {
                    filter      = current.filterText.ifBlank { null }
                    tipoTercero = 1  // solo clientes (tipo 1)
                }
                val pageable = PageRequest.of(current.currentPage, current.pageSize, Sort.unsorted())
                val result   = service.findAllPaginate(idData, filterDto, pageable)
                _state.update {
                    it.copy(
                        isLoading = false,
                        clientes  = result.content ?: emptyList(),
                        paginator = result.paginator
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error desconocido") }
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
        val current   = _state.value.currentPage
        if (current < paginator.totalPages - 1) {
            _state.update { it.copy(currentPage = current + 1) }
            cargar()
        }
    }

    fun abrirFormulario(idCliente: UUID? = null) =
        _state.update { it.copy(showForm = true, editingId = idCliente) }

    fun cerrarFormulario() {
        _state.update { it.copy(showForm = false, editingId = null) }
        cargar()
    }

    fun dismissError() = _state.update { it.copy(errorMessage = null) }

    fun onDestroy() = scope.cancel()
}
