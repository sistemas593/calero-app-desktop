package com.calero.lili.desktop.ui.ventas.facturas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Edit
import com.calero.lili.core.enums.Liquidar
import com.calero.lili.core.enums.TipoIngreso
import com.calero.lili.core.modComprasItems.dto.GeItemGetListDto
import com.calero.lili.core.modComprasItemsImpuesto.dto.GeImpuestoResponseDto
import java.math.BigDecimal

// ── Paleta ────────────────────────────────────────────────────────────────────
private val FColorHeader = Color(0xFF1565C0)
private val FColorBorde  = Color(0xFFDDE3F0)
private val FColorTexto  = Color(0xFF1A1A2E)
private val FColorSub    = Color(0xFF6B7A99)
private val FColorBg     = Color(0xFFF0F4FF)
private val FColorExito  = Color(0xFF2E7D32)

// ── Helpers ───────────────────────────────────────────────────────────────────
private fun nombreImpuesto(imp: GeImpuestoResponseDto): String {
    val tipo = when (imp.codigo) { "2" -> "IVA"; "3" -> "ICE"; "5" -> "IRBPNR"; else -> "Imp ${imp.codigo}" }
    val pct  = when (imp.codigoPorcentaje) {
        "0"  -> "0%"; "2" -> "12%"; "3" -> "14%"; "4" -> "15%"; "5" -> "5%"
        "6"  -> "No Objeto"; "7" -> "Exento"; "8" -> "Diferenciado"; "10" -> "13%"
        else -> imp.codigoPorcentaje ?: ""
    }
    return if (pct.isNotEmpty()) "$tipo - $pct" else tipo
}

private fun labelTipoIngreso(ti: TipoIngreso) = when (ti) {
    TipoIngreso.VL -> "Ventas locales"
    TipoIngreso.AF -> "Activos fijos"
    TipoIngreso.RG -> "Reembolso de gastos"
    TipoIngreso.EX -> "Exportaciones"
}

// ── Screen principal ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaFormScreen(viewModel: FacturaFormViewModel, onCerrar: () -> Unit) {
    val state by viewModel.state.collectAsState()

    if (state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title            = { Text("Error") },
            text             = { Text(state.errorMessage ?: "") },
            confirmButton    = { TextButton(onClick = viewModel::dismissError) { Text("Aceptar") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(FColorBg)) {

        // ── Barra de título (fija)
        Row(
            modifier              = Modifier.fillMaxWidth().background(FColorHeader).padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                if (state.isEditMode) "Editar Factura" else "Nueva Factura",
                color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onCerrar) { Text("✕ Cerrar", color = Color.White, fontSize = 13.sp) }
        }

        if (state.successMessage != null) {
            Text(
                state.successMessage ?: "",
                color    = FColorExito, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }

        if (state.isLoading) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FColorHeader)
            }
        } else {
            // ── Contenido scrollable
            Column(
                modifier            = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SeccionDocumento(state, viewModel)
                SeccionAdquiriente(state, viewModel)
                SeccionDetalle(state, viewModel)
                SeccionPagoYCampos(state, viewModel)
            }
        }

        // ── Barra de acciones (fija)
        HorizontalDivider(color = FColorBorde)
        Row(
            modifier              = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onCerrar, enabled = !state.isSaving) { Text("Cancelar") }
            Spacer(Modifier.width(12.dp))
            if (state.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = FColorHeader)
                Spacer(Modifier.width(8.dp))
            }
            Button(
                onClick = { viewModel.guardar(onCerrar) },
                enabled = !state.isSaving,
                colors  = ButtonDefaults.buttonColors(containerColor = FColorHeader)
            ) { Text("Guardar") }
        }
    }
}

// ── Sección 1: Documento ──────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeccionDocumento(state: FacturaFormUiState, vm: FacturaFormViewModel) {
    SeccionCard {
        SeccionTitulo("Documento")

        // Fila: Serie · Secuencial · Fecha
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Bottom) {
            var expandedSerie by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded        = expandedSerie,
                onExpandedChange = { expandedSerie = !expandedSerie },
                modifier        = Modifier.width(150.dp)
            ) {
                OutlinedTextField(
                    value         = state.serie.ifBlank { "" },
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Serie *", fontSize = 12.sp) },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expandedSerie) },
                    modifier      = Modifier.menuAnchor().fillMaxWidth(),
                    textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                    colors        = fieldColors()
                )
                ExposedDropdownMenu(expanded = expandedSerie, onDismissRequest = { expandedSerie = false }) {
                    if (state.seriesDisponibles.isEmpty()) {
                        DropdownMenuItem(
                            text    = { Text("Sin series disponibles", fontSize = 13.sp, color = FColorSub) },
                            onClick = { expandedSerie = false }
                        )
                    } else {
                        state.seriesDisponibles.forEach { serie ->
                            DropdownMenuItem(
                                text    = { Text(serie.serie, fontSize = 13.sp) },
                                onClick = { vm.seleccionarSerie(serie); expandedSerie = false }
                            )
                        }
                    }
                }
            }
            FormField("Secuencial *", state.secuencial,  vm::setSecuencial, Modifier.width(180.dp))
            FormField("Fecha emisión", state.fechaEmision, {}, Modifier.width(200.dp), readOnly = true)
        }

        // Fila: Ambiente
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Bottom) {
            var expandedAmb by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expandedAmb, onExpandedChange = { expandedAmb = !expandedAmb }, modifier = Modifier.width(160.dp)) {
                OutlinedTextField(
                    value = if (state.ambiente == 1) "Pruebas" else "Producción", onValueChange = {}, readOnly = true,
                    label = { Text("Ambiente", fontSize = 12.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedAmb) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp), colors = fieldColors()
                )
                ExposedDropdownMenu(expanded = expandedAmb, onDismissRequest = { expandedAmb = false }) {
                    DropdownMenuItem(text = { Text("Pruebas",    fontSize = 13.sp) }, onClick = { vm.setAmbiente(1); expandedAmb = false })
                    DropdownMenuItem(text = { Text("Producción", fontSize = 13.sp) }, onClick = { vm.setAmbiente(2); expandedAmb = false })
                }
            }
        }
    }
}

// ── Sección 2: Adquiriente ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeccionAdquiriente(state: FacturaFormUiState, vm: FacturaFormViewModel) {
    SeccionCard {
        SeccionTitulo("Adquiriente")

        // Búsqueda por identificación — igual al campo Tercero del listado
        val terceroSeleccionado = state.idTercero != null
        Box {
            OutlinedTextField(
                value         = state.numeroIdentificacion,
                onValueChange = vm::setNumeroIdentificacionBusqueda,
                label         = { Text("Identificación *", fontSize = 12.sp) },
                singleLine    = true,
                readOnly      = terceroSeleccionado,
                modifier      = Modifier.width(260.dp),
                textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                colors        = fieldColors(),
                trailingIcon  = {
                    when {
                        state.buscandoTercero ->
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        state.numeroIdentificacion.isNotBlank() ->
                            IconButton(onClick = vm::limpiarTercero, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Clear, "Limpiar", modifier = Modifier.size(14.dp))
                            }
                    }
                }
            )
            if (!terceroSeleccionado) {
                DropdownMenu(
                    expanded         = state.terceroDropdownVisible,
                    onDismissRequest = vm::cerrarDropdownTercero,
                    modifier         = Modifier.width(380.dp).heightIn(max = 240.dp)
                ) {
                    state.terceroSugerencias.forEach { t ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(t.tercero ?: "—", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text("${t.tipoIdentificacion} · ${t.numeroIdentificacion ?: "—"}", fontSize = 11.sp, color = FColorSub)
                                }
                            },
                            onClick = { vm.seleccionarTercero(t) }
                        )
                    }
                }
            }
        }

        FormField("Razón social *", state.terceroNombre, vm::setTerceroNombre, Modifier.width(580.dp), readOnly = terceroSeleccionado)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Bottom) {
            FormField("Dirección",  state.direccion, vm::setDireccion, Modifier.width(380.dp), readOnly = terceroSeleccionado)
            FormField("Teléfono",   state.telefonos, vm::setTelefonos, Modifier.width(190.dp), readOnly = terceroSeleccionado)
        }

        FormField("Correo electrónico", state.email, vm::setEmail, Modifier.width(380.dp), readOnly = terceroSeleccionado)
    }
}

// ── Sección 3: Detalle ────────────────────────────────────────────────────────
@Composable
private fun SeccionDetalle(state: FacturaFormUiState, vm: FacturaFormViewModel) {

    // Diálogo de búsqueda de item (fuera del Surface para evitar corrupción de slot table)
    if (state.showItemDialog) {
        DialogoItem(state = state, vm = vm)
    }

    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp,
        color           = Color.White
    ) {
        Column {
            // Encabezado: título + botón
            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Detalle", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FColorHeader)
                Button(
                    onClick        = vm::agregarDetalle,
                    colors         = ButtonDefaults.buttonColors(containerColor = FColorHeader),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) { Text("+ Agregar item", fontSize = 13.sp) }
            }

            HorizontalDivider(color = FColorBorde)

            val scrollH = rememberScrollState()

            // Cabecera de la tabla
            Box(modifier = Modifier.horizontalScroll(scrollH)) {
                Row(modifier = Modifier.background(FColorHeader).padding(vertical = 8.dp)) {
                    CabeceraCelda("Cód. Principal",  120.dp)
                    CabeceraCelda("Cód. Auxiliar",   100.dp)
                    CabeceraCelda("Cantidad",         80.dp)
                    CabeceraCelda("Descripción",     180.dp)
                    CabeceraCelda("Precio unitario", 110.dp)
                    CabeceraCelda("Tarifa",          110.dp)
                    CabeceraCelda("Descuento",        90.dp)
                    CabeceraCelda("Valor total",     100.dp)
                    CabeceraCelda("Acciones",         80.dp)
                }
            }

            if (state.detalle.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                    Text("Sin items. Pulse '+ Agregar item'.", color = FColorSub, fontSize = 13.sp)
                }
            } else {
                state.detalle.forEachIndexed { idx, d ->
                    key(d.key) {
                        Box(modifier = Modifier.horizontalScroll(scrollH)) {
                            FilaDetalle(
                                d  = d,
                                bg = if (idx % 2 == 0) Color(0xFFF5F7FF) else Color.White,
                                vm = vm
                            )
                        }
                        HorizontalDivider(color = FColorBorde, thickness = 0.5.dp)
                    }
                }
            }

        }
    }
}

@Composable
private fun FilaDetalle(
    d:  DetalleItemUi,
    bg: Color,
    vm: FacturaFormViewModel
) {
    Row(
        modifier          = Modifier.background(bg).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cód. Principal (120dp)
        Text(
            text     = d.codigoPrincipal.ifBlank { "—" },
            fontSize = 12.sp, color = FColorTexto, maxLines = 1,
            modifier = Modifier.width(120.dp).padding(horizontal = 6.dp)
        )
        // Cód. Auxiliar (100dp)
        Text(
            text     = d.codigoAuxiliar.ifBlank { "—" },
            fontSize = 12.sp, color = FColorTexto, maxLines = 1,
            modifier = Modifier.width(100.dp).padding(horizontal = 6.dp)
        )
        // Cantidad (80dp) — editable
        FilaCampoDecimal(d.cantidad, 80.dp) { vm.setDetalleCantidad(d.key, it) }
        // Descripción (180dp)
        Text(
            text     = d.descripcion.ifBlank { "—" },
            fontSize = 11.sp, color = FColorTexto, maxLines = 2,
            modifier = Modifier.width(180.dp).padding(horizontal = 6.dp)
        )
        // Precio unitario (110dp) — editable
        FilaCampoDecimal(d.precioUnitario, 110.dp) { vm.setDetallePrecio(d.key, it) }
        // Tarifa (110dp) — read-only, from impuesto
        Text(
            text     = d.impuesto?.let { nombreImpuesto(it) } ?: "—",
            fontSize = 11.sp, color = FColorTexto, maxLines = 1,
            modifier = Modifier.width(110.dp).padding(horizontal = 6.dp)
        )
        // Descuento (90dp) — editable
        FilaCampoDecimal(d.descuento, 90.dp) { vm.setDetalleDescuento(d.key, it) }
        // Valor total (100dp) — read-only
        Text(
            text      = "%.2f".format(d.subtotalItem),
            fontSize  = 12.sp, color = FColorTexto, textAlign = TextAlign.End,
            modifier  = Modifier.width(100.dp).padding(horizontal = 6.dp)
        )
        // Acciones (80dp)
        Row(modifier = Modifier.width(80.dp), horizontalArrangement = Arrangement.Center) {
            IconButton(onClick = { vm.abrirDialogoItem(d.key) }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Edit, "Editar", tint = FColorHeader, modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = { vm.eliminarDetalle(d.key) }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, "Eliminar", tint = Color(0xFFB00020), modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ── Diálogo: Búsqueda de item ─────────────────────────────────────────────────
@Composable
private fun DialogoItem(state: FacturaFormUiState, vm: FacturaFormViewModel) {
    AlertDialog(
        onDismissRequest = vm::cerrarDialogoItem,
        title = { Text("Buscar item", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text  = {
            Column(
                modifier            = Modifier.width(520.dp).heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Campo de búsqueda
                OutlinedTextField(
                    value         = state.itemDialogQuery,
                    onValueChange = vm::setItemDialogQuery,
                    label         = { Text("Código o descripción", fontSize = 12.sp) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                    colors        = fieldColors(),
                    trailingIcon  = {
                        if (state.itemDialogBuscando)
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    }
                )

                if (state.itemDialogResultados.isNotEmpty()) {
                    // Cabecera tabla
                    Row(modifier = Modifier.fillMaxWidth().background(FColorHeader).padding(vertical = 6.dp)) {
                        Text("Código",      color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(120.dp).padding(horizontal = 6.dp))
                        Text("Descripción", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(horizontal = 6.dp))
                        Text("Precio",      color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp).padding(horizontal = 6.dp), textAlign = TextAlign.End)
                    }
                    // Filas de resultados
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        state.itemDialogResultados.forEachIndexed { idx, item ->
                            Row(
                                modifier          = Modifier
                                    .fillMaxWidth()
                                    .background(if (idx % 2 == 0) Color(0xFFF5F7FF) else Color.White)
                                    .clickable { vm.seleccionarItemDesdeDialogo(item) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text     = item.codigoPrincipal ?: "—",
                                    fontSize = 12.sp, color = FColorTexto, maxLines = 1,
                                    modifier = Modifier.width(120.dp).padding(horizontal = 6.dp)
                                )
                                Text(
                                    text     = item.descripcion ?: "—",
                                    fontSize = 12.sp, color = FColorTexto, maxLines = 2,
                                    modifier = Modifier.weight(1f).padding(horizontal = 6.dp)
                                )
                                Text(
                                    text      = "%.2f".format(item.precios?.firstOrNull()?.precio1 ?: java.math.BigDecimal.ZERO),
                                    fontSize  = 12.sp, color = FColorTexto, textAlign = TextAlign.End,
                                    modifier  = Modifier.width(90.dp).padding(horizontal = 6.dp)
                                )
                            }
                            HorizontalDivider(color = FColorBorde, thickness = 0.5.dp)
                        }
                    }
                } else if (!state.itemDialogBuscando && state.itemDialogQuery.isNotBlank()) {
                    Box(Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                        Text("Sin resultados.", color = FColorSub, fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = vm::cerrarDialogoItem) { Text("Cerrar") }
        }
    )
}

// ── Sección 4+5: Pago, Campos Adicionales y Totales ──────────────────────────
@Composable
private fun SeccionPagoYCampos(state: FacturaFormUiState, vm: FacturaFormViewModel) {

    if (state.showFormaPagoDialog)     { DialogoFormaPago(state = state, vm = vm) }
    if (state.showCampoAdicionalDialog) { DialogoCampoAdicional(state = state, vm = vm) }

    val propinaValor = if (state.propina10)
        state.subtotal.multiply(java.math.BigDecimal("0.10")).setScale(2, java.math.RoundingMode.HALF_UP)
    else java.math.BigDecimal.ZERO
    val valorAPagar = state.total.add(propinaValor)

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment     = Alignment.Top
    ) {
        // ── Columna izquierda ─────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Formas de pago
            Surface(
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(10.dp),
                shadowElevation = 1.dp,
                color           = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Formas de pago", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FColorHeader)
                    HorizontalDivider(color = FColorBorde)

                    // Cabecera tabla
                    Row(modifier = Modifier.fillMaxWidth().background(FColorHeader).padding(vertical = 8.dp)) {
                        Text("Forma de Pago", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(2f).padding(horizontal = 8.dp))
                        Text("Valor",         color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp).padding(horizontal = 8.dp), textAlign = TextAlign.End)
                        Text("Plazo",         color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(70.dp).padding(horizontal = 8.dp))
                        Text("Tiempo",        color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(80.dp).padding(horizontal = 8.dp))
                        Text("Acciones",      color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(70.dp).padding(horizontal = 8.dp))
                    }

                    if (state.formasPagoSri.isEmpty()) {
                        Box(Modifier.fillMaxWidth().height(50.dp), contentAlignment = Alignment.Center) {
                            Text("No existen formas de pago", color = FColorSub, fontSize = 12.sp)
                        }
                    } else {
                        state.formasPagoSri.forEachIndexed { idx, fp ->
                            key(fp.key) {
                                Row(
                                    modifier          = Modifier.fillMaxWidth().background(if (idx % 2 == 0) Color(0xFFF5F7FF) else Color.White).padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(fp.descripcion.ifBlank { fp.formaPago }, fontSize = 12.sp, color = FColorTexto, modifier = Modifier.weight(2f).padding(horizontal = 8.dp), maxLines = 2)
                                    Text("%.2f".format(fp.total), fontSize = 12.sp, color = FColorTexto, textAlign = TextAlign.End, modifier = Modifier.width(90.dp).padding(horizontal = 8.dp))
                                    Text(fp.plazo.ifBlank { "—" },        fontSize = 12.sp, color = FColorTexto, modifier = Modifier.width(70.dp).padding(horizontal = 8.dp))
                                    Text(fp.unidadTiempo.ifBlank { "—" }, fontSize = 12.sp, color = FColorTexto, modifier = Modifier.width(80.dp).padding(horizontal = 8.dp))
                                    Box(modifier = Modifier.width(70.dp), contentAlignment = Alignment.Center) {
                                        IconButton(onClick = { vm.eliminarFormaPagoSri(fp.key) }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.Delete, "Eliminar", tint = Color(0xFFB00020), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                                HorizontalDivider(color = FColorBorde, thickness = 0.5.dp)
                            }
                        }
                    }

                    // Añadir forma de pago
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Button(
                            onClick        = { vm.abrirDialogoFormaPago() },
                            colors         = ButtonDefaults.buttonColors(containerColor = FColorHeader),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) { Text("+ Añadir forma de pago", fontSize = 12.sp) }
                    }
                }
            }

            // Campos adicionales
            Surface(
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(10.dp),
                shadowElevation = 1.dp,
                color           = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Campos adicionales", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FColorHeader)
                    HorizontalDivider(color = FColorBorde)

                    // Cabecera tabla
                    Row(modifier = Modifier.fillMaxWidth().background(FColorHeader).padding(vertical = 8.dp)) {
                        Text("Nombre",      color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
                        Text("Descripcion", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(2f).padding(horizontal = 8.dp))
                        Text("Acciones",    color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(70.dp).padding(horizontal = 8.dp))
                    }

                    if (state.camposAdicionales.isEmpty()) {
                        Box(Modifier.fillMaxWidth().height(50.dp), contentAlignment = Alignment.Center) {
                            Text("No existen campos adicionales", color = FColorSub, fontSize = 12.sp)
                        }
                    } else {
                        state.camposAdicionales.forEachIndexed { idx, ca ->
                            key(ca.key) {
                                Row(
                                    modifier          = Modifier.fillMaxWidth().background(if (idx % 2 == 0) Color(0xFFF5F7FF) else Color.White).padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(ca.nombre, fontSize = 12.sp, color = FColorTexto, modifier = Modifier.weight(1f).padding(horizontal = 8.dp), maxLines = 2)
                                    Text(ca.valor,  fontSize = 12.sp, color = FColorTexto, modifier = Modifier.weight(2f).padding(horizontal = 8.dp), maxLines = 3)
                                    Box(modifier = Modifier.width(70.dp), contentAlignment = Alignment.Center) {
                                        IconButton(onClick = { vm.eliminarCampoAdicional(ca.key) }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.Delete, "Eliminar", tint = Color(0xFFB00020), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                                HorizontalDivider(color = FColorBorde, thickness = 0.5.dp)
                            }
                        }
                    }

                    Button(
                        onClick        = vm::abrirDialogoCampoAdicional,
                        colors         = ButtonDefaults.buttonColors(containerColor = FColorHeader),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) { Text("+ Añadir campo adicional", fontSize = 12.sp) }
                }
            }
        }

        // ── Columna derecha: Totales ──────────────────────────────────────────
        Surface(
            modifier        = Modifier.width(320.dp),
            shape           = RoundedCornerShape(10.dp),
            shadowElevation = 1.dp,
            color           = Color.White
        ) {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                // Cabecera tabla
                Row(modifier = Modifier.fillMaxWidth().background(FColorHeader).padding(vertical = 8.dp)) {
                    Text("Detalle",  color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(horizontal = 10.dp))
                    Text("Valores",  color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp).padding(horizontal = 10.dp), textAlign = TextAlign.End)
                }

                FilaTotalDetalle("Subtotal sin impuestos:", state.subtotal)
                FilaTotalDetalle("Subtotal 15%:",            state.subtotal15)
                FilaTotalDetalle("Subtotal 5%:",             state.subtotal5)
                FilaTotalDetalle("Subtotal tarifa especial:", state.subtotalTarifaEspecial)
                FilaTotalDetalle("Subtotal 0%:",             state.subtotal0)
                FilaTotalDetalle("Subtotal no objeto de IVA:", state.subtotalNoObjeto)
                FilaTotalDetalle("Subtotal exento de IVA:",  state.subtotalExento)
                FilaTotalDetalle("Total descuento:",         state.totalDescuento)
                FilaTotalDetalle("Valor ICE:",               state.valorIce)
                FilaTotalDetalle("IVA 15% :",                state.iva15)
                FilaTotalDetalle("IVA 5% :",                 state.iva5)
                FilaTotalDetalle("IVA tarifa especial:",     state.ivaTarifaEspecial)

                // Propina 10%
                Row(
                    modifier          = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Propina 10%:", fontSize = 12.sp, color = FColorTexto, modifier = Modifier.weight(1f))
                    Checkbox(
                        checked         = state.propina10,
                        onCheckedChange = { vm.togglePropina() },
                        modifier        = Modifier.size(20.dp)
                    )
                }

                HorizontalDivider(color = FColorBorde, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))

                // Valor a pagar (negrita)
                Row(
                    modifier          = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Valor a pagar:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FColorTexto, modifier = Modifier.weight(1f))
                    Text("%.2f".format(valorAPagar), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FColorHeader,
                        modifier = Modifier.width(90.dp).padding(horizontal = 10.dp), textAlign = TextAlign.End)
                }
            }
        }
    }
}

// ── Diálogo: Detalle forma de pago ────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoFormaPago(state: FacturaFormUiState, vm: FacturaFormViewModel) {
    AlertDialog(
        onDismissRequest = vm::cerrarDialogoFormaPago,
        title = { Text("Detalle forma de pago", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.width(400.dp)) {

                // Forma de Pago dropdown
                var expandedFp by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expandedFp, onExpandedChange = { expandedFp = !expandedFp }) {
                    OutlinedTextField(
                        value         = state.dialogFpDescripcion.ifBlank { "Seleccione" },
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Forma de Pago *", fontSize = 12.sp) },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expandedFp) },
                        modifier      = Modifier.menuAnchor().fillMaxWidth(),
                        textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                        colors        = fieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded         = expandedFp,
                        onDismissRequest = { expandedFp = false },
                        modifier         = Modifier.heightIn(max = 280.dp)
                    ) {
                        state.formasPagoSriDisponibles.forEach { fp ->
                            DropdownMenuItem(
                                text    = { Text(fp.formaPagoSri ?: fp.codigoFormaPagoSri ?: "—", fontSize = 13.sp) },
                                onClick = {
                                    vm.setDialogFpSeleccion(fp.codigoFormaPagoSri ?: "", fp.formaPagoSri ?: "")
                                    expandedFp = false
                                }
                            )
                        }
                    }
                }

                // Valor
                OutlinedTextField(
                    value         = state.dialogFpValor.toPlainString(),
                    onValueChange = { it.toBigDecimalOrNull()?.let { v -> vm.setDialogFpValor(v) } },
                    label         = { Text("Valor *", fontSize = 12.sp) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp, textAlign = TextAlign.End),
                    colors        = fieldColors()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Plazo
                    OutlinedTextField(
                        value         = state.dialogFpPlazo,
                        onValueChange = vm::setDialogFpPlazo,
                        label         = { Text("Plazo", fontSize = 12.sp) },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f),
                        textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                        colors        = fieldColors()
                    )

                    // Unidad de tiempo
                    var expandedUT by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expandedUT, onExpandedChange = { expandedUT = !expandedUT }, modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value         = state.dialogFpUnidadTiempo.ifBlank { "Días" },
                            onValueChange = {},
                            readOnly      = true,
                            label         = { Text("Tiempo", fontSize = 12.sp) },
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expandedUT) },
                            modifier      = Modifier.menuAnchor().fillMaxWidth(),
                            textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                            colors        = fieldColors()
                        )
                        ExposedDropdownMenu(expanded = expandedUT, onDismissRequest = { expandedUT = false }) {
                            listOf("Días", "Meses", "Años").forEach { u ->
                                DropdownMenuItem(text = { Text(u, fontSize = 13.sp) }, onClick = { vm.setDialogFpUnidadTiempo(u); expandedUT = false })
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = vm::confirmarFormaPago,
                colors  = ButtonDefaults.buttonColors(containerColor = FColorHeader)
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = vm::cerrarDialogoFormaPago) { Text("Cancelar") }
        }
    )
}

// ── Diálogo: Campo adicional ──────────────────────────────────────────────────
@Composable
private fun DialogoCampoAdicional(state: FacturaFormUiState, vm: FacturaFormViewModel) {
    AlertDialog(
        onDismissRequest = vm::cerrarDialogoCampoAdicional,
        title = { Text("Detalle Campo Adicional", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text  = {
            Column(
                modifier            = Modifier.width(400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value         = state.dialogCaNombre,
                    onValueChange = vm::setDialogCaNombre,
                    label         = { Text("Nombre *", fontSize = 12.sp) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                    colors        = fieldColors()
                )
                OutlinedTextField(
                    value         = state.dialogCaValor,
                    onValueChange = vm::setDialogCaValor,
                    label         = { Text("Descripción", fontSize = 12.sp) },
                    singleLine    = false,
                    minLines      = 3,
                    maxLines      = 5,
                    modifier      = Modifier.fillMaxWidth(),
                    textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                    colors        = fieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = vm::confirmarCampoAdicional,
                colors  = ButtonDefaults.buttonColors(containerColor = FColorHeader)
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = vm::cerrarDialogoCampoAdicional) { Text("Cancelar") }
        }
    )
}

@Composable
private fun FilaTotalDetalle(label: String, valor: BigDecimal) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = FColorTexto, modifier = Modifier.weight(1f))
        Text("%.2f".format(valor), fontSize = 12.sp, color = FColorTexto,
            modifier = Modifier.width(90.dp).padding(horizontal = 10.dp), textAlign = TextAlign.End)
    }
}

// ── Composables auxiliares ────────────────────────────────────────────────────
@Composable
private fun SeccionCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp,
        color           = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
private fun SeccionTitulo(texto: String) {
    Column {
        Text(texto, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FColorHeader)
        HorizontalDivider(color = FColorBorde, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun FormField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, readOnly: Boolean = false) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        singleLine = true, modifier = modifier,
        textStyle  = LocalTextStyle.current.copy(fontSize = 13.sp),
        colors     = fieldColors(),
        readOnly   = readOnly
    )
}

@Composable
private fun FilaCampoTexto(value: String, ancho: Dp, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onChange, singleLine = true,
        modifier  = Modifier.width(ancho).padding(horizontal = 4.dp),
        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
        colors    = fieldColors()
    )
}

@Composable
private fun FilaCampoDecimal(value: BigDecimal, ancho: Dp, onChange: (BigDecimal) -> Unit) {
    var text by remember { mutableStateOf(value.toPlainString()) }
    // Sincronizar si el valor externo cambia y no coincide con lo escrito
    val valueStr = value.toPlainString()
    if (text.toBigDecimalOrNull()?.compareTo(value) != 0 && text != "" && !text.endsWith(".")) {
        text = valueStr
    }
    OutlinedTextField(
        value         = text,
        onValueChange = { nuevo ->
            text = nuevo
            nuevo.toBigDecimalOrNull()?.let { v -> onChange(v) }
        },
        singleLine    = true,
        modifier      = Modifier.width(ancho).padding(horizontal = 4.dp),
        textStyle     = LocalTextStyle.current.copy(fontSize = 12.sp, textAlign = TextAlign.End),
        colors        = fieldColors()
    )
}

@Composable
private fun CabeceraCelda(texto: String, ancho: Dp) {
    Text(texto, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.width(ancho).padding(horizontal = 6.dp))
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = FColorHeader,
    unfocusedBorderColor = FColorBorde,
    focusedLabelColor    = FColorHeader
)
