package com.calero.lili.desktop.ui.items

import com.calero.lili.core.modComprasItems.GeItemsServiceImpl
import com.calero.lili.core.modComprasItems.dto.GeMedidasItemsDto
import com.calero.lili.core.modComprasItems.dto.GeItemRequestDto
import com.calero.lili.core.modComprasItemsMedidas.GeItemsMedidasServiceImpl
import com.calero.lili.core.modComprasItemsMedidas.dto.GeItemMedidaListFilterDto
import com.calero.lili.core.modComprasItemsMedidas.dto.GeItemMedidaReportDto
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestoItemsServiceImpl
import com.calero.lili.core.modComprasItemsImpuesto.dto.GeImpuestoResponseDto
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
import java.math.BigDecimal
import java.util.UUID

data class DetalleAdicionalUi(val nombre: String = "", val valor: String = "")

data class ItemFormUiState(
    val codigoPrincipal: String = "",
    val codigoAuxiliar: String = "",
    val codigoBarras: String = "",
    val descripcion: String = "",
    val caracteristicas: String = "",
    val medidasDisponibles   : List<GeItemMedidaReportDto>  = emptyList(),
    val medidaSeleccionada   : GeItemMedidaReportDto?       = null,
    val impuestosDisponibles : List<GeImpuestoResponseDto>  = emptyList(),
    val impuestoSeleccionado : GeImpuestoResponseDto?       = null,
    val precio: String = "",
    val detallesAdicionales: List<DetalleAdicionalUi> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isEditMode: Boolean = false
)

class ItemFormViewModel(
    private val service          : GeItemsServiceImpl,
    private val medidasService   : GeItemsMedidasServiceImpl,
    private val impuestosService : GeImpuestoItemsServiceImpl,
    private val idItem           : UUID? = null,
    private val idData           : Long = 1L,
    private val idEmpresa        : Long = 1L
) {

    private val _state = MutableStateFlow(ItemFormUiState(isEditMode = idItem != null))
    val state: StateFlow<ItemFormUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val USUARIO = "desktop-user"
    }

    init {
        cargarMedidasDisponibles()
        cargarImpuestosDisponibles()
        if (idItem != null) cargarItem(idItem)
    }

    private fun cargarImpuestosDisponibles() {
        scope.launch {
            try {
                val lista = impuestosService.findAll()
                _state.update { it.copy(impuestosDisponibles = lista) }
            } catch (_: Exception) {}
        }
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
                val primeraIdImpuesto = dto.impuestos?.firstOrNull()?.idImpuesto
                val impuestoPrevio = if (primeraIdImpuesto != null)
                    _state.value.impuestosDisponibles.firstOrNull { it.idImpuesto == primeraIdImpuesto }
                else null
                val precioCargado = dto.precios?.firstOrNull()?.precio1
                    ?.takeIf { it.compareTo(BigDecimal.ZERO) != 0 }
                    ?.toPlainString() ?: ""
                val detalles = dto.detallesAdicionales
                    ?.map { DetalleAdicionalUi(it.nombre ?: "", it.valor ?: "") } ?: emptyList()
                _state.update {
                    it.copy(
                        isLoading            = false,
                        codigoPrincipal      = dto.codigoPrincipal ?: "",
                        codigoAuxiliar       = dto.codigoAuxiliar ?: "",
                        codigoBarras         = dto.codigoBarras ?: "",
                        descripcion          = dto.descripcion ?: "",
                        caracteristicas      = dto.caracteristicas ?: "",
                        medidaSeleccionada   = medidaPrevia,
                        impuestoSeleccionado = impuestoPrevio,
                        precio               = precioCargado,
                        detallesAdicionales  = detalles
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Error al cargar item: ${e.message}") }
            }
        }
    }

    private fun parsePrecio(v: String): BigDecimal? =
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
        val impuestos = current.impuestoSeleccionado?.let { imp ->
            listOf(GeItemRequestDto.Impuesto.builder().idImpuesto(imp.idImpuesto).build())
        }
        val precios = if (current.precio.isNotBlank()) {
            listOf(
                GeItemRequestDto.Precios(
                    parsePrecio(current.precio),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
                )
            )
        } else null
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
            .impuestos(impuestos)
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
    fun setImpuesto(v: GeImpuestoResponseDto?) = _state.update { it.copy(impuestoSeleccionado = v) }
    fun setPrecio(v: String)                   = _state.update { it.copy(precio = v) }

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
