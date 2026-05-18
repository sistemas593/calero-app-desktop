package com.calero.lili.desktop.ui.ventas.notasCredito

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calero.lili.core.modComprasItems.dto.GeItemGetListDto
import com.calero.lili.core.modComprasItemsImpuesto.dto.GeImpuestoResponseDto
import com.calero.lili.desktop.ui.ventas.facturas.PdfViewerDialog
import java.math.BigDecimal

// ── Paleta ────────────────────────────────────────────────────────────────────
private val NcColorHeader = Color(0xFF1565C0)
private val NcColorBorde  = Color(0xFFDDE3F0)
private val NcColorTexto  = Color(0xFF1A1A2E)
private val NcColorSub    = Color(0xFF6B7A99)
private val NcColorBg     = Color(0xFFF0F4FF)
private val NcColorExito  = Color(0xFF2E7D32)

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

private object NcDateMaskTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter(Char::isDigit).take(8)
        val out = StringBuilder()
        for (i in digits.indices) {
            if (i == 2 || i == 4) out.append('/')
            out.append(digits[i])
        }
        val outLen = out.length
        val offsetMap = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                when (val d = offset.coerceIn(0, digits.length)) {
                    in 0..2 -> d
                    in 3..4 -> d + 1
                    else    -> d + 2
                }.coerceIn(0, outLen)

            override fun transformedToOriginal(offset: Int): Int =
                when (val t = offset.coerceIn(0, outLen)) {
                    in 0..2 -> t
                    in 3..5 -> t - 1
                    else    -> t - 2
                }.coerceIn(0, digits.length)
        }
        return TransformedText(AnnotatedString(out.toString()), offsetMap)
    }
}

// ── Screen principal ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotaCreditoFormScreen(viewModel: NotaCreditoFormViewModel, onCerrar: () -> Unit) {
    val state by viewModel.state.collectAsState()

    if (state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title            = { Text("Error") },
            text             = { Text(state.errorMessage ?: "") },
            confirmButton    = { TextButton(onClick = viewModel::dismissError) { Text("Aceptar") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(NcColorBg)) {

        // ── Barra de título (fija)
        Row(
            modifier              = Modifier.fillMaxWidth().background(NcColorHeader).padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                if (state.isEditMode) "Editar Nota de Crédito" else "Nueva Nota de Crédito",
                color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onCerrar) { Text("✕ Cerrar", color = Color.White, fontSize = 13.sp) }
        }

        // ── Visor PDF
        if (state.showPdfViewer) {
            PdfViewerDialog(
                pdfBytes    = state.pdfBytes,
                isLoading   = state.pdfLoading,
                nombre      = state.pdfNombre,
                onImprimir  = viewModel::imprimirPdfActual,
                onDescargar = viewModel::descargarPdfActual,
                onCerrar    = { viewModel.dismissPdfViewer(); onCerrar() }
            )
        }

        // ── Mensaje de fallback
        if (state.successMessage != null) {
            Text(
                state.successMessage ?: "",
                color    = NcColorExito, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }

        if (state.isLoading) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NcColorHeader)
            }
        } else {
            Column(
                modifier            = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SeccionDocumento(state, viewModel)
                SeccionMotivo(state, viewModel)
                SeccionAdquiriente(state, viewModel)
                SeccionDetalle(state, viewModel)
                SeccionPagoYCampos(state, viewModel)
            }
        }

        // ── Barra de acciones (fija)
        HorizontalDivider(color = NcColorBorde)
        Row(
            modifier              = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onCerrar, enabled = !state.isSaving) { Text("Cancelar") }
            Spacer(Modifier.width(12.dp))
            if (state.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = NcColorHeader)
                Spacer(Modifier.width(8.dp))
            }
            Button(
                onClick = { viewModel.guardar(onCerrar) },
                enabled = !state.isSaving,
                colors  = ButtonDefaults.buttonColors(containerColor = NcColorHeader)
            ) { Text("Guardar") }
        }
    }
}

// ── Sección 1: Documento ──────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeccionDocumento(state: NotaCreditoFormUiState, vm: NotaCreditoFormViewModel) {
    SeccionCard {
        SeccionTitulo("Documento")

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Bottom) {
            var expandedSerie by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded         = expandedSerie,
                onExpandedChange = { expandedSerie = !expandedSerie },
                modifier         = Modifier.width(280.dp)
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
                            text    = { Text("Sin series disponibles", fontSize = 13.sp, color = NcColorSub) },
                            onClick = { expandedSerie = false }
                        )
                    } else {
                        state.seriesDisponibles.forEach { serie ->
                            DropdownMenuItem(
                                text    = { Text("${serie.serie} | ${serie.nombreComercial ?: ""}", fontSize = 13.sp) },
                                onClick = { vm.seleccionarSerie(serie); expandedSerie = false }
                            )
                        }
                    }
                }
            }
            FormField("Secuencial *", state.secuencial, {}, Modifier.width(180.dp), readOnly = true)
            FormField("Fecha emisión", state.fechaEmision, {}, Modifier.width(200.dp), readOnly = true)
        }

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

// ── Sección 2: Motivo y Documento Relacionado (exclusiva NCR) ────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeccionMotivo(state: NotaCreditoFormUiState, vm: NotaCreditoFormViewModel) {
    SeccionCard {
        SeccionTitulo("Motivo / Documento Relacionado")

        // Campo concepto (textarea)
        OutlinedTextField(
            value         = state.concepto,
            onValueChange = vm::setConcepto,
            label         = { Text("Concepto *", fontSize = 12.sp) },
            singleLine    = false,
            minLines      = 2,
            maxLines      = 4,
            modifier      = Modifier.fillMaxWidth(),
            textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
            colors        = fieldColors()
        )

        // Fila: tipo documento relacionado + serie + secuencial
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Bottom) {
            val tiposDoc = listOf(
                "01" to "Factura",
                "03" to "Liquidación de compra",
                "05" to "Nota de Débito"
            )
            var expandedTipo by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded         = expandedTipo,
                onExpandedChange = { expandedTipo = !expandedTipo },
                modifier         = Modifier.width(260.dp)
            ) {
                OutlinedTextField(
                    value         = tiposDoc.find { it.first == state.modCodigoDocumento }?.second ?: state.modCodigoDocumento,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Tipo Doc. Relacionado *", fontSize = 12.sp) },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expandedTipo) },
                    modifier      = Modifier.menuAnchor().fillMaxWidth(),
                    textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                    colors        = fieldColors()
                )
                ExposedDropdownMenu(expanded = expandedTipo, onDismissRequest = { expandedTipo = false }) {
                    tiposDoc.forEach { (codigo, label) ->
                        DropdownMenuItem(
                            text    = { Text(label, fontSize = 13.sp) },
                            onClick = { vm.setModCodigoDocumento(codigo); expandedTipo = false }
                        )
                    }
                }
            }
            FormField("Serie Relacionada *",      state.modSerie,      vm::setModSerie,      Modifier.width(160.dp))
            FormField("Secuencial Relacionado *", state.modSecuencial, vm::setModSecuencial, Modifier.width(200.dp))
        }

        // Fecha del documento relacionado con máscara dd/MM/yyyy
        OutlinedTextField(
            value                = state.modFechaEmision,
            onValueChange        = { vm.setModFechaEmision(it.filter(Char::isDigit).take(8)) },
            label                = { Text("Fecha Doc. Relacionado * (dd/MM/yyyy)", fontSize = 12.sp) },
            singleLine           = true,
            visualTransformation = NcDateMaskTransformation,
            modifier             = Modifier.width(280.dp),
            textStyle            = LocalTextStyle.current.copy(fontSize = 13.sp),
            colors               = fieldColors()
        )
    }
}

// ── Sección 3: Adquiriente ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeccionAdquiriente(state: NotaCreditoFormUiState, vm: NotaCreditoFormViewModel) {
    SeccionCard {
        SeccionTitulo("Adquiriente")

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
                                    Text("${t.tipoIdentificacion} · ${t.numeroIdentificacion ?: "—"}", fontSize = 11.sp, color = NcColorSub)
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
            FormField("Dirección", state.direccion, vm::setDireccion, Modifier.width(380.dp), readOnly = terceroSeleccionado)
            FormField("Teléfono",  state.telefonos, vm::setTelefonos, Modifier.width(190.dp), readOnly = terceroSeleccionado)
        }

        FormField("Correo electrónico", state.email, vm::setEmail, Modifier.width(380.dp), readOnly = terceroSeleccionado)
    }
}

// ── Sección 4: Detalle ────────────────────────────────────────────────────────
@Composable
private fun SeccionDetalle(state: NotaCreditoFormUiState, vm: NotaCreditoFormViewModel) {

    if (state.showItemDialog) {
        DialogoItem(state = state, vm = vm)
    }

    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp,
        color           = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Detalle", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NcColorHeader)
                Button(
                    onClick        = vm::agregarDetalle,
                    colors         = ButtonDefaults.buttonColors(containerColor = NcColorHeader),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) { Text("+ Agregar item", fontSize = 13.sp) }
            }

            HorizontalDivider(color = NcColorBorde)

            Row(modifier = Modifier.fillMaxWidth().background(NcColorHeader).padding(vertical = 8.dp)) {
                Text("Cód. Principal",  color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.5f).padding(horizontal = 8.dp))
                Text("Cód. Auxiliar",  color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f).padding(horizontal = 8.dp))
                Text("Cantidad",       color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
                Text("Descripción",    color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(2.2f).padding(horizontal = 8.dp))
                Text("Precio unitario",color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.4f).padding(horizontal = 8.dp))
                Text("Tarifa",         color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.3f).padding(horizontal = 8.dp))
                Text("Descuento",      color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.1f).padding(horizontal = 8.dp))
                Text("Valor total",    color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f).padding(horizontal = 8.dp))
                Text("Acciones",       color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(70.dp).padding(horizontal = 8.dp))
            }

            if (state.detalle.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                    Text("Sin items. Pulse '+ Agregar item'.", color = NcColorSub, fontSize = 13.sp)
                }
            } else {
                state.detalle.forEachIndexed { idx, d ->
                    key(d.key) {
                        FilaDetalle(
                            d  = d,
                            bg = if (idx % 2 == 0) Color(0xFFF5F7FF) else Color.White,
                            vm = vm
                        )
                        HorizontalDivider(color = NcColorBorde, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaDetalle(d: NcDetalleItemUi, bg: Color, vm: NotaCreditoFormViewModel) {
    Row(
        modifier          = Modifier.fillMaxWidth().background(bg).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(d.codigoPrincipal.ifBlank { "—" }, fontSize = 12.sp, color = NcColorTexto, maxLines = 1, modifier = Modifier.weight(1.5f).padding(horizontal = 8.dp))
        Text(d.codigoAuxiliar.ifBlank { "—" },  fontSize = 12.sp, color = NcColorTexto, maxLines = 1, modifier = Modifier.weight(1.2f).padding(horizontal = 8.dp))
        FilaCampoDecimal(d.cantidad,       Modifier.weight(1f))    { vm.setDetalleCantidad(d.key, it) }
        Text(d.descripcion.ifBlank { "—" }, fontSize = 11.sp, color = NcColorTexto, maxLines = 2, modifier = Modifier.weight(2.2f).padding(horizontal = 8.dp))
        FilaCampoDecimal(d.precioUnitario, Modifier.weight(1.4f))  { vm.setDetallePrecio(d.key, it) }
        Text(d.impuesto?.let { nombreImpuesto(it) } ?: "—", fontSize = 11.sp, color = NcColorTexto, maxLines = 1, modifier = Modifier.weight(1.3f).padding(horizontal = 8.dp))
        FilaCampoDecimal(d.descuento,      Modifier.weight(1.1f))  { vm.setDetalleDescuento(d.key, it) }
        Text("%.2f".format(d.subtotalItem), fontSize = 12.sp, color = NcColorTexto, textAlign = TextAlign.End, modifier = Modifier.weight(1.2f).padding(horizontal = 8.dp))
        Row(modifier = Modifier.width(70.dp), horizontalArrangement = Arrangement.Center) {
            IconButton(onClick = { vm.abrirDialogoItem(d.key) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, "Editar", tint = NcColorHeader, modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = { vm.eliminarDetalle(d.key) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, "Eliminar", tint = Color(0xFFB00020), modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ── Diálogo: Búsqueda de item ─────────────────────────────────────────────────
@Composable
private fun DialogoItem(state: NotaCreditoFormUiState, vm: NotaCreditoFormViewModel) {
    AlertDialog(
        onDismissRequest = vm::cerrarDialogoItem,
        title = { Text("Buscar item", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text  = {
            Column(modifier = Modifier.width(520.dp).heightIn(max = 420.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value         = state.itemDialogQuery,
                    onValueChange = vm::setItemDialogQuery,
                    label         = { Text("Código o descripción", fontSize = 12.sp) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                    colors        = fieldColors(),
                    trailingIcon  = {
                        if (state.itemDialogBuscando) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    }
                )

                if (state.itemDialogResultados.isNotEmpty()) {
                    Row(modifier = Modifier.fillMaxWidth().background(NcColorHeader).padding(vertical = 6.dp)) {
                        Text("Código",      color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(120.dp).padding(horizontal = 6.dp))
                        Text("Descripción", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(horizontal = 6.dp))
                        Text("Precio",      color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp).padding(horizontal = 6.dp), textAlign = TextAlign.End)
                    }
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
                                Text(item.codigoPrincipal ?: "—", fontSize = 12.sp, color = NcColorTexto, maxLines = 1, modifier = Modifier.width(120.dp).padding(horizontal = 6.dp))
                                Text(item.descripcion ?: "—",     fontSize = 12.sp, color = NcColorTexto, maxLines = 2, modifier = Modifier.weight(1f).padding(horizontal = 6.dp))
                                Text("%.2f".format(item.precios?.firstOrNull()?.precio1 ?: java.math.BigDecimal.ZERO),
                                    fontSize = 12.sp, color = NcColorTexto, textAlign = TextAlign.End, modifier = Modifier.width(90.dp).padding(horizontal = 6.dp))
                            }
                            HorizontalDivider(color = NcColorBorde, thickness = 0.5.dp)
                        }
                    }
                } else if (!state.itemDialogBuscando && state.itemDialogQuery.isNotBlank()) {
                    Box(Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                        Text("Sin resultados.", color = NcColorSub, fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = vm::cerrarDialogoItem) { Text("Cerrar") } }
    )
}

// ── Sección 5: Pago, Campos Adicionales y Totales ─────────────────────────────
@Composable
private fun SeccionPagoYCampos(state: NotaCreditoFormUiState, vm: NotaCreditoFormViewModel) {

    if (state.showCampoAdicionalDialog) { DialogoCampoAdicional(state = state, vm = vm) }

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment     = Alignment.Top
    ) {
        // ── Columna izquierda ─────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Campos adicionales
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), shadowElevation = 1.dp, color = Color.White) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Campos adicionales", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NcColorHeader)
                    HorizontalDivider(color = NcColorBorde)

                    Row(modifier = Modifier.fillMaxWidth().background(NcColorHeader).padding(vertical = 8.dp)) {
                        Text("Nombre",      color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
                        Text("Descripción", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(2f).padding(horizontal = 8.dp))
                        Text("Acciones",    color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(70.dp).padding(horizontal = 8.dp))
                    }

                    if (state.camposAdicionales.isEmpty()) {
                        Box(Modifier.fillMaxWidth().height(50.dp), contentAlignment = Alignment.Center) {
                            Text("No existen campos adicionales", color = NcColorSub, fontSize = 12.sp)
                        }
                    } else {
                        state.camposAdicionales.forEachIndexed { idx, ca ->
                            key(ca.key) {
                                Row(
                                    modifier          = Modifier.fillMaxWidth().background(if (idx % 2 == 0) Color(0xFFF5F7FF) else Color.White).padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(ca.nombre, fontSize = 12.sp, color = NcColorTexto, modifier = Modifier.weight(1f).padding(horizontal = 8.dp), maxLines = 2)
                                    Text(ca.valor,  fontSize = 12.sp, color = NcColorTexto, modifier = Modifier.weight(2f).padding(horizontal = 8.dp), maxLines = 3)
                                    Box(modifier = Modifier.width(70.dp), contentAlignment = Alignment.Center) {
                                        IconButton(onClick = { vm.eliminarCampoAdicional(ca.key) }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.Delete, "Eliminar", tint = Color(0xFFB00020), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                                HorizontalDivider(color = NcColorBorde, thickness = 0.5.dp)
                            }
                        }
                    }

                    Button(
                        onClick        = vm::abrirDialogoCampoAdicional,
                        colors         = ButtonDefaults.buttonColors(containerColor = NcColorHeader),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) { Text("+ Añadir campo adicional", fontSize = 12.sp) }
                }
            }
        }

        // ── Columna derecha: Totales ──────────────────────────────────────────
        Surface(modifier = Modifier.width(320.dp), shape = RoundedCornerShape(10.dp), shadowElevation = 1.dp, color = Color.White) {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Row(modifier = Modifier.fillMaxWidth().background(NcColorHeader).padding(vertical = 8.dp)) {
                    Text("Detalle", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(horizontal = 10.dp))
                    Text("Valores", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(90.dp).padding(horizontal = 10.dp), textAlign = TextAlign.End)
                }

                FilaTotalDetalle("Subtotal sin impuestos:", state.subtotal)
                FilaTotalDetalle("Subtotal 15%:",            state.subtotal15)
                FilaTotalDetalle("Subtotal 5%:",             state.subtotal5)
                FilaTotalDetalle("Subtotal tarifa especial:", state.subtotalTarifaEspecial)
                FilaTotalDetalle("Subtotal 0%:",             state.subtotal0)
                FilaTotalDetalle("Subtotal no objeto IVA:",  state.subtotalNoObjeto)
                FilaTotalDetalle("Subtotal exento IVA:",     state.subtotalExento)
                FilaTotalDetalle("Total descuento:",         state.totalDescuento)
                FilaTotalDetalle("IVA 15%:",                 state.iva15)
                FilaTotalDetalle("IVA 5%:",                  state.iva5)
                FilaTotalDetalle("IVA tarifa especial:",     state.ivaTarifaEspecial)

                HorizontalDivider(color = NcColorBorde, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))

                Row(
                    modifier          = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Valor a pagar:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NcColorTexto, modifier = Modifier.weight(1f))
                    Text("%.2f".format(state.total), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NcColorHeader,
                        modifier = Modifier.width(90.dp).padding(horizontal = 10.dp), textAlign = TextAlign.End)
                }
            }
        }
    }
}

// ── Diálogo: Campo adicional ──────────────────────────────────────────────────
@Composable
private fun DialogoCampoAdicional(state: NotaCreditoFormUiState, vm: NotaCreditoFormViewModel) {
    AlertDialog(
        onDismissRequest = vm::cerrarDialogoCampoAdicional,
        title = { Text("Detalle Campo Adicional", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text  = {
            Column(modifier = Modifier.width(400.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.dialogCaNombre, onValueChange = vm::setDialogCaNombre,
                    label = { Text("Nombre *", fontSize = 12.sp) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), textStyle = LocalTextStyle.current.copy(fontSize = 13.sp), colors = fieldColors()
                )
                OutlinedTextField(
                    value = state.dialogCaValor, onValueChange = vm::setDialogCaValor,
                    label = { Text("Descripción", fontSize = 12.sp) }, singleLine = false, minLines = 3, maxLines = 5,
                    modifier = Modifier.fillMaxWidth(), textStyle = LocalTextStyle.current.copy(fontSize = 13.sp), colors = fieldColors()
                )
            }
        },
        confirmButton = {
            Button(onClick = vm::confirmarCampoAdicional, colors = ButtonDefaults.buttonColors(containerColor = NcColorHeader)) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = vm::cerrarDialogoCampoAdicional) { Text("Cancelar") } }
    )
}

// ── Composables auxiliares ────────────────────────────────────────────────────
@Composable
private fun FilaTotalDetalle(label: String, valor: BigDecimal) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 12.sp, color = NcColorTexto, modifier = Modifier.weight(1f))
        Text("%.2f".format(valor), fontSize = 12.sp, color = NcColorTexto,
            modifier = Modifier.width(90.dp).padding(horizontal = 10.dp), textAlign = TextAlign.End)
    }
}

@Composable
private fun SeccionCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), shadowElevation = 1.dp, color = Color.White) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
private fun SeccionTitulo(texto: String) {
    Column {
        Text(texto, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NcColorHeader)
        HorizontalDivider(color = NcColorBorde, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun FormField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, readOnly: Boolean = false) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        singleLine = true, modifier = modifier,
        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
        colors    = fieldColors(),
        readOnly  = readOnly
    )
}

@Composable
private fun FilaCampoDecimal(value: BigDecimal, modifier: Modifier = Modifier, onChange: (BigDecimal) -> Unit) {
    var text by remember { mutableStateOf(value.toPlainString()) }
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
        modifier      = modifier.padding(horizontal = 4.dp),
        textStyle     = LocalTextStyle.current.copy(fontSize = 12.sp, textAlign = TextAlign.End),
        colors        = fieldColors()
    )
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = NcColorHeader,
    unfocusedBorderColor = NcColorBorde,
    focusedLabelColor    = NcColorHeader
)
