package com.calero.lili.desktop.ui.items

import com.calero.lili.core.dtos.Paginator
import com.calero.lili.core.modComprasItems.GeItemsServiceImpl
import com.calero.lili.core.modComprasItems.dto.GeItemGetListDto
import com.calero.lili.core.modComprasItems.dto.GeItemListFilterDto
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

data class ItemsUiState(
    val isLoading: Boolean = false,
    val items: List<GeItemGetListDto> = emptyList(),
    val paginator: Paginator? = null,
    val filterText: String = "",
    val currentPage: Int = 0,
    val pageSize: Int = 10,
    val errorMessage: String? = null,
    val showForm: Boolean = false,
    val editingId: UUID? = null
)

class ItemsViewModel(
    private val service: GeItemsServiceImpl,
    private val idData: Long = 1L,
    private val idEmpresa: Long = 1L
) {

    private val _state = MutableStateFlow(ItemsUiState())
    val state: StateFlow<ItemsUiState> = _state.asStateFlow()

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
                val filterDto = GeItemListFilterDto().apply {
                    filter = current.filterText.ifBlank { null }
                }
                val pageable = PageRequest.of(current.currentPage, current.pageSize, Sort.unsorted())
                val result = service.findAllPaginate(idData, idEmpresa, filterDto, pageable)
                _state.update {
                    it.copy(
                        isLoading = false,
                        items     = result.content ?: emptyList(),
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
        val current   = _state.value.currentPage
        if (current < paginator.totalPages - 1) {
            _state.update { it.copy(currentPage = current + 1) }
            cargar()
        }
    }

    fun abrirFormulario(idItem: UUID? = null) {
        _state.update { it.copy(showForm = true, editingId = idItem) }
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
