package com.calero.lili.desktop.ui.items.medidas

import com.calero.lili.core.modComprasItemsMedidas.GeItemsMedidasServiceImpl
import com.calero.lili.core.modComprasItemsMedidas.dto.GeItemMedidaCreationRequestDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class MedidaFormUiState(
    val unidadMedida: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isEditMode: Boolean = false
)

class MedidaFormViewModel(
    private val service: GeItemsMedidasServiceImpl,
    private val idMedida: UUID? = null,
    private val idData: Long = 1L
) {

    private val _state = MutableStateFlow(MedidaFormUiState(isEditMode = idMedida != null))
    val state: StateFlow<MedidaFormUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val USUARIO = "desktop-user"
    }

    init {
        if (idMedida != null) cargarMedida(idMedida)
    }

    private fun cargarMedida(id: UUID) {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val dto = service.findFirstById(idData, id)
                _state.update { it.copy(isLoading = false, unidadMedida = dto.unidadMedida ?: "") }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Error al cargar medida: ${e.message}") }
            }
        }
    }

    fun guardar() {
        val current = _state.value
        if (current.unidadMedida.isBlank()) {
            _state.update { it.copy(errorMessage = "La unidad de medida es requerida") }
            return
        }
        val request = GeItemMedidaCreationRequestDto().apply {
            unidadMedida = current.unidadMedida.trim()
        }
        scope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            try {
                if (idMedida != null) {
                    service.update(idData, idMedida, request, USUARIO)
                    _state.update { it.copy(isSaving = false, successMessage = "Medida actualizada correctamente") }
                } else {
                    service.create(idData, request, USUARIO)
                    _state.update { it.copy(isSaving = false, successMessage = "Medida creada correctamente") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, errorMessage = e.message ?: "Error al guardar la medida") }
            }
        }
    }

    fun setUnidadMedida(v: String) = _state.update { it.copy(unidadMedida = v, errorMessage = null) }
    fun dismissError() = _state.update { it.copy(errorMessage = null) }
    fun onDestroy() = scope.cancel()
}
