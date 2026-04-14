package com.calero.lili.desktop.ui.ventas.facturas

import com.calero.lili.core.modVentas.VtVentaEntity
import com.calero.lili.core.modVentas.facturas.VtVentasFacturasServiceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EnviarAAutorizarUiState(
    val facturas     : List<VtVentaEntity> = emptyList(),
    val isLoading    : Boolean             = false,
    val isProcesando : Boolean             = false,
    val resultado    : String?             = null,
    val error        : String?             = null
)

class EnviarAAutorizarViewModel(
    private val service   : VtVentasFacturasServiceImpl,
    private val idData    : Long,
    private val idEmpresa : Long
) {
    private val _state = MutableStateFlow(EnviarAAutorizarUiState(isLoading = true))
    val state: StateFlow<EnviarAAutorizarUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init { cargar() }

    fun cargar() {
        scope.launch {
            _state.update { it.copy(isLoading = true, error = null, resultado = null) }
            try {
                val lista = service.getFacturasAutorizar(idData, idEmpresa)
                _state.update { it.copy(isLoading = false, facturas = lista) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Error al cargar") }
            }
        }
    }

    fun procesarTodas() {
        val facturas = _state.value.facturas.toList()
        scope.launch {
            _state.update { it.copy(isProcesando = true, resultado = null, error = null) }
            var errores = 0
            for (factura in facturas) {
                try {
                    service.procesarUnaFactura(idData, idEmpresa, factura)
                    _state.update { s -> s.copy(facturas = s.facturas.filter { it.idVenta != factura.idVenta }) }
                } catch (e: Exception) {
                    errores++
                }
            }
            val msg = if (errores == 0) "Todas las facturas procesadas correctamente"
                      else "${facturas.size - errores} procesada(s), $errores con error"
            _state.update { it.copy(isProcesando = false, resultado = msg) }
        }
    }

    fun dismissResultado() = _state.update { it.copy(resultado = null, error = null) }

    fun onDestroy() = scope.cancel()
}
