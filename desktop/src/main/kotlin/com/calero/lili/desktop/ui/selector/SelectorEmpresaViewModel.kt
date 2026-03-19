package com.calero.lili.desktop.ui.selector

import com.calero.lili.core.modAdminEmpresas.AdEmpresasServiceImpl
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaGetListDto
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaListFilterDto
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

data class SelectorEmpresaUiState(
    val empresas: List<AdEmpresaGetListDto> = emptyList(),
    val busqueda: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SelectorEmpresaViewModel(
    private val service: AdEmpresasServiceImpl,
    private val idData: Long = 1L
) {
    private val _state = MutableStateFlow(SelectorEmpresaUiState(isLoading = true))
    val state: StateFlow<SelectorEmpresaUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        cargarEmpresas()
    }

    private fun cargarEmpresas() {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val pageable = PageRequest.of(0, 200, Sort.by("razonSocial").ascending())
                val result   = service.findAllPaginate(idData, AdEmpresaListFilterDto(), pageable)
                _state.update { it.copy(isLoading = false, empresas = result.content ?: emptyList()) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Error al cargar empresas: ${e.message}") }
            }
        }
    }

    fun setBusqueda(texto: String) = _state.update { it.copy(busqueda = texto) }

    fun empresasFiltradas(): List<AdEmpresaGetListDto> {
        val q = _state.value.busqueda.trim().lowercase()
        return if (q.isEmpty()) _state.value.empresas
        else _state.value.empresas.filter {
            it.razonSocial?.lowercase()?.contains(q) == true ||
            it.ruc?.contains(q) == true
        }
    }

    fun onDestroy() = scope.cancel()
}
