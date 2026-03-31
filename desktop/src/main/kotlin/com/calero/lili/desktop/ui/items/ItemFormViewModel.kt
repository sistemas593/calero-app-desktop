package com.calero.lili.desktop.ui.items

import com.calero.lili.core.modComprasItems.GeItemsServiceImpl
import com.calero.lili.core.modComprasItems.dto.GeMedidasItemsDto
import com.calero.lili.core.modComprasItems.dto.GeItemRequestDto
import com.calero.lili.core.modComprasItemsMedidas.GeItemsMedidasServiceImpl
import com.calero.lili.core.modComprasItemsMedidas.dto.GeItemMedidaListFilterDto
import com.calero.lili.core.modComprasItemsMedidas.dto.GeItemMedidaReportDto
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

data class DetalleAdicionalUi(val nombre: String = "", val valor: String = "")

data class PreciosUi(
    val precio1: String = "",
    val precio2: String = "",
    val precio3: String = "",
    val precio4: String = "",
    val precio5: String = ""
)

data class ItemFormUiState(
    val codigoPrincipal: String = "",
    val codigoAuxiliar: String = "",
    val codigoBarras: String = "",
    val descripcion: String = "",
    val caracteristicas: String = "",
    val medidasDisponibles: List<GeItemMedidaReportDto> = emptyList(),
    val medidaSeleccionada: GeItemMedidaReportDto? = null,
    val precios: List<PreciosUi> = emptyList(),
    val detallesAdicionales: List<DetalleAdicionalUi> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isEditMode: Boolean = false
)

class ItemFormViewModel(
    private val service: GeItemsServiceImpl,
    private val medidasService: GeItemsMedidasServiceImpl,
    private val idItem: UUID? = null,
    private val idData: Long = 1L,
    private val idEmpresa: Long = 1L
) {

    private val _state = MutableStateFlow(ItemFormUiState(isEditMode = idItem != null))
    val state: StateFlow<ItemFormUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val USUARIO = "desktop-user"
    }

    init {
        cargarMedidasDisponibles()
        if (idItem != null) cargarItem(idItem)
    }

    private fun cargarMedidasDisponibles() {
        scope.launch {
            try {
                val pageable = PageRequest.of(0, 200, Sort.unsorted())
                val result = medidasService.findAllPaginate(idData, GeItemMedidaListFilterDto(), pageable)
                _state.update { it.copy(medidasDisponibles = result.content ?: emptyList()) }
            } catch (_: Exception) {}
        }
    }

    private fun cargarItem(id: UUID) {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val dto = service.findById(idData, idEmpresa, id)
                val primeraIdMedida = dto.medidas?.firstOrNull()?.idMedida
                val medidaPrevia = if (primeraIdMedida != null)
                    _state.value.medidasDisponibles.firstOrNull { it.idUnidadMedida == primeraIdMedida }
                else null
                val preciosCargados = dto.precios?.map { p ->
                    PreciosUi(
                        precio1 = p.precio1?.toPlainString() ?: "",
                        precio2 = p.precio2?.toPlainString() ?: "",
                        precio3 = p.precio3?.toPlainString() ?: "",
                        precio4 = p.precio4?.toPlainString() ?: "",
                        precio5 = p.precio5?.toPlainString() ?: ""
                    )
                } ?: emptyList()
                val detalles = dto.detallesAdicionales
                    ?.map { DetalleAdicionalUi(it.nombre ?: "", it.valor ?: "") } ?: emptyList()
                _state.update {
                    it.copy(
                        isLoading           = false,
                        codigoPrincipal     = dto.codigoPrincipal ?: "",
                        codigoAuxiliar      = dto.codigoAuxiliar ?: "",
                        codigoBarras        = dto.codigoBarras ?: "",
                        descripcion         = dto.descripcion ?: "",
                        caracteristicas     = dto.caracteristicas ?: "",
                        medidaSeleccionada  = medidaPrevia,
                        precios             = preciosCargados,
                        detallesAdicionales = detalles
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Error al cargar item: ${e.message}") }
            }
        }
    }

    private fun parsePrecio(v: String): java.math.BigDecimal? =
        v.trim().replace(",", ".").toBigDecimalOrNull()

    fun guardar() {
        val current = _state.value
        if (current.codigoPrincipal.isBlank()) {
            _state.update { it.copy(errorMessage = "El código principal es requerido") }
            return
        }
        if (current.descripcion.isBlank()) {
            _state.update { it.copy(errorMessage = "La descripción es requerida") }
            return
        }
        val medidas = current.medidaSeleccionada?.let { m ->
            listOf(GeMedidasItemsDto.builder().idMedida(m.idUnidadMedida).factor(1).build())
        }
        val precios = current.precios
            .filter { p -> listOf(p.precio1, p.precio2, p.precio3, p.precio4, p.precio5).any { it.isNotBlank() } }
            .map { p ->
                GeItemRequestDto.Precios(
                    parsePrecio(p.precio1),
                    parsePrecio(p.precio2),
                    parsePrecio(p.precio3),
                    parsePrecio(p.precio4),
                    parsePrecio(p.precio5)
                )
            }.ifEmpty { null }
        val detalles = current.detallesAdicionales
            .filter { it.nombre.isNotBlank() }
            .map { GeItemRequestDto.DetalleAdicional.builder()
                .nombre(it.nombre.trim()).valor(it.valor.trim()).build() }
            .ifEmpty { null }
        val request = GeItemRequestDto.builder()
            .codigoPrincipal(current.codigoPrincipal.trim())
            .codigoAuxiliar(current.codigoAuxiliar.trim().ifBlank { null })
            .codigoBarras(current.codigoBarras.trim().ifBlank { null })
            .descripcion(current.descripcion.trim())
            .tipoItem("SER")
            .caracteristicas(current.caracteristicas.trim().ifBlank { null })
            .medidas(medidas)
            .precios(precios)
            .detallesAdicionales(detalles)
            .build()
        scope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            try {
                if (idItem != null) {
                    service.update(idData, idEmpresa, idItem, request, USUARIO)
                    _state.update { it.copy(isSaving = false, successMessage = "Item actualizado correctamente") }
                } else {
                    service.create(idData, idEmpresa, request, USUARIO)
                    _state.update { it.copy(isSaving = false, successMessage = "Item creado correctamente") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, errorMessage = e.message ?: "Error al guardar el item") }
            }
        }
    }

    fun setCodigoPrincipal(v: String)         = _state.update { it.copy(codigoPrincipal = v, errorMessage = null) }
    fun setCodigoAuxiliar(v: String)           = _state.update { it.copy(codigoAuxiliar = v) }
    fun setCodigoBarras(v: String)             = _state.update { it.copy(codigoBarras = v) }
    fun setDescripcion(v: String)              = _state.update { it.copy(descripcion = v, errorMessage = null) }
    fun setCaracteristicas(v: String)          = _state.update { it.copy(caracteristicas = v) }
    fun setMedida(v: GeItemMedidaReportDto?)   = _state.update { it.copy(medidaSeleccionada = v) }

    fun agregarPrecio() = _state.update {
        it.copy(precios = it.precios + PreciosUi())
    }
    fun eliminarPrecio(idx: Int) = _state.update {
        it.copy(precios = it.precios.toMutableList().also { l -> l.removeAt(idx) })
    }
    fun setPrecio1(idx: Int, v: String) = _state.update {
        it.copy(precios = it.precios.toMutableList().also { l -> l[idx] = l[idx].copy(precio1 = v) })
    }
    fun setPrecio2(idx: Int, v: String) = _state.update {
        it.copy(precios = it.precios.toMutableList().also { l -> l[idx] = l[idx].copy(precio2 = v) })
    }
    fun setPrecio3(idx: Int, v: String) = _state.update {
        it.copy(precios = it.precios.toMutableList().also { l -> l[idx] = l[idx].copy(precio3 = v) })
    }
    fun setPrecio4(idx: Int, v: String) = _state.update {
        it.copy(precios = it.precios.toMutableList().also { l -> l[idx] = l[idx].copy(precio4 = v) })
    }
    fun setPrecio5(idx: Int, v: String) = _state.update {
        it.copy(precios = it.precios.toMutableList().also { l -> l[idx] = l[idx].copy(precio5 = v) })
    }

    fun agregarDetalle() = _state.update {
        it.copy(detallesAdicionales = it.detallesAdicionales + DetalleAdicionalUi())
    }
    fun eliminarDetalle(idx: Int) = _state.update {
        it.copy(detallesAdicionales = it.detallesAdicionales
            .toMutableList().also { l -> l.removeAt(idx) })
    }
    fun setDetalleNombre(idx: Int, v: String) = _state.update {
        it.copy(detallesAdicionales = it.detallesAdicionales
            .toMutableList().also { l -> l[idx] = l[idx].copy(nombre = v) })
    }
    fun setDetalleValor(idx: Int, v: String) = _state.update {
        it.copy(detallesAdicionales = it.detallesAdicionales
            .toMutableList().also { l -> l[idx] = l[idx].copy(valor = v) })
    }

    fun dismissError() = _state.update { it.copy(errorMessage = null) }
    fun onDestroy()    = scope.cancel()
}
