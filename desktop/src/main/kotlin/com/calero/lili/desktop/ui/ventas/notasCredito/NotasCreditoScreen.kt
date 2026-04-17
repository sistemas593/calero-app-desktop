package com.calero.lili.desktop.ui.ventas.notasCredito

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calero.lili.core.modVentas.dto.GetListDto
import com.calero.lili.core.modVentas.dto.ResponseValoresDto
import java.math.BigDecimal

// ── Colores ──────────────────────────────────────────────────────────────────
private val ColorHeader    = Color(0xFF1565C0)
private val ColorFondo     = Color(0xFFF0F4FF)
private val ColorBorde     = Color(0xFFDDE3F0)
private val ColorFilaPar   = Color(0xFFF5F7FF)
private val ColorFilaImpar = Color.White
private val ColorTexto     = Color(0xFF1A1A2E)
private val ColorSub       = Color(0xFF6B7A99)

// ── Definición de columnas ────────────────────────────────────────────────────
private data class Columna(val titulo: String, val ancho: Dp)

private val COLUMNAS = listOf(
    Columna("Tipo Venta",         90.dp),
    Columna("Ambiente",           90.dp),
    Columna("Estado",             100.dp),
    Columna("Serie",              80.dp),
    Columna("Secuencial",         100.dp),
    Columna("N. Autorización",    210.dp),
    Columna("Fecha Emisión",      110.dp),
    Columna("N. Identificación",  130.dp),
    Columna("Nombre Cliente",     200.dp),
    Columna("Email",              180.dp),
    Columna("Estado Email",       110.dp),
    Columna("Subtotal",           90.dp),
    Columna("Descuento",          90.dp),
    Columna("Base Cero",          90.dp),
    Columna("Base No Objeto",     110.dp),
    Columna("Base Excenta",       100.dp),
    Columna("Base Grav. 5%",      100.dp),
    Columna("Base Grav. 8%",      100.dp),
    Columna("Base Grav. 15%",     100.dp),
    Columna("Total",              100.dp),
    Columna("Acciones",           120.dp)
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun NotasCreditoScreen(
    viewModel: NotasCreditoViewModel,
    onNuevaNotaCredito: () -> Unit = {},
    onEditarNotaCredito: (java.util.UUID) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    // ── Diálogo de error general
    if (state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title            = { Text("Error") },
            text             = { Text(state.errorMessage ?: "") },
            confirmButton    = { TextButton(onClick = viewModel::dismissError) { Text("Aceptar") } }
        )
    }

    // ── Diálogo resultado de firma (éxito)
    if (state.firmaResultado != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissFirmaResultado,
            title            = { Text("Firma Electrónica") },
            text             = { Text(state.firmaResultado ?: "") },
            confirmButton    = { TextButton(onClick = viewModel::dismissFirmaResultado) { Text("Aceptar") } }
        )
    }

    // ── Diálogo resultado XML/PDF
    if (state.xmlPdfResultado != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissXmlPdfResultado,
            title            = { Text("Descarga") },
            text             = { Text(state.xmlPdfResultado ?: "") },
            confirmButton    = {
                TextButton(onClick = viewModel::dismissXmlPdfResultado) { Text("Aceptar") }
            }
        )
    }

    // ── Diálogo resultado de exportación
    if (state.exportResultado != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissExportResultado,
            title            = { Text("Exportar Excel") },
            text             = { Text(state.exportResultado ?: "") },
            confirmButton    = {
                TextButton(onClick = viewModel::dismissExportResultado) { Text("Aceptar") }
            }
        )
    }

    // ── Diálogo de firma electrónica
    if (state.firmaDialogNota != null) {
        DialogoFirma(
            serie      = state.firmaDialogNota?.serie ?: "",
            secuencial = state.firmaDialogNota?.secuencial ?: "",
            firmando   = state.firmando,
            firmaError = state.firmaError,
            onProcesar = viewModel::procesarFirma,
            onCancelar = viewModel::cerrarDialogoFirma
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(20.dp)
    ) {
        // ── Título
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text("Notas de Crédito", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ColorTexto)
                Text("Listado de ventas / notas de crédito", fontSize = 13.sp, color = ColorSub)
            }
            Button(
                onClick = onNuevaNotaCredito,
                colors  = ButtonDefaults.buttonColors(containerColor = ColorHeader)
            ) { Text("+ Nueva Nota de Crédito", fontSize = 13.sp) }
        }

        Spacer(Modifier.height(12.dp))

        // ── Barra de filtros
        FiltrosBar(state = state, viewModel = viewModel)

        Spacer(Modifier.height(12.dp))

        // ── Tabla con scroll horizontal
        Surface(
            modifier        = Modifier.weight(1f).fillMaxWidth(),
            shadowElevation = 2.dp,
            color           = Color.White,
            shape           = MaterialTheme.shapes.small
        ) {
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ColorHeader, strokeWidth = 3.dp)
                }
            } else {
                val scrollH    = rememberScrollState()
                val scrollV    = rememberScrollState()
                val anchoTotal = COLUMNAS.fold(0.dp) { acc, col -> acc + col.ancho }

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 12.dp, end = 12.dp)
                    ) {
                        // ── Cabecera
                        Box(modifier = Modifier.horizontalScroll(scrollH)) {
                            Row(
                                modifier = Modifier
                                    .width(anchoTotal)
                                    .background(ColorHeader)
                                    .padding(vertical = 10.dp)
                            ) {
                                COLUMNAS.forEach { col -> CeldaHeader(col.titulo, col.ancho) }
                            }
                        }
                        HorizontalDivider(color = ColorBorde, thickness = 1.dp)

                        if (state.notasCredito.isEmpty()) {
                            Box(
                                Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No se encontraron notas de crédito", color = ColorSub, fontSize = 14.sp)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(scrollH)
                                    .verticalScroll(scrollV)
                            ) {
                                Column(modifier = Modifier.width(anchoTotal)) {
                                    state.notasCredito.forEachIndexed { idx, nota ->
                                        FilaNota(
                                            idx      = idx,
                                            f        = nota,
                                            cargando = state.xmlPdfCargando == nota.idVenta,
                                            onEditar = { nota.idVenta?.let { onEditarNotaCredito(it) } },
                                            onFirmar = { viewModel.abrirDialogoFirma(nota) },
                                            onXml      = { viewModel.descargarXml(nota) },
                                            onPdf      = { viewModel.descargarPdf(nota) },
                                            onImprimir = { viewModel.imprimirPdf(nota) }
                                        )
                                        HorizontalDivider(color = ColorBorde, thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }

                    HorizontalScrollbar(
                        adapter  = rememberScrollbarAdapter(scrollH),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(end = 12.dp, bottom = 1.dp)
                    )

                    VerticalScrollbar(
                        adapter  = rememberScrollbarAdapter(scrollV),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .fillMaxHeight()
                            .padding(bottom = 12.dp, top = 1.dp)
                    )
                }
            }
        }

        // ── Paginador
        state.paginator?.let { pag ->
            Spacer(Modifier.height(12.dp))
            Paginador(
                currentPage  = state.currentPage,
                totalPages   = pag.totalPages ?: 1,
                totalItems   = pag.totalElements ?: 0,
                onAnterior   = { if (state.currentPage > 0) viewModel.irPagina(state.currentPage - 1) },
                onSiguiente  = { if (state.currentPage < (pag.totalPages ?: 1) - 1) viewModel.irPagina(state.currentPage + 1) }
            )
        }

        // ── Exportar
        Spacer(Modifier.height(8.dp))
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            if (state.isExporting) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color       = ColorHeader
                )
                Spacer(Modifier.width(8.dp))
            }
            Button(
                onClick  = viewModel::exportarExcel,
                enabled  = !state.isExporting,
                colors   = ButtonDefaults.buttonColors(containerColor = ColorHeader)
            ) {
                Text("Exportar Excel", fontSize = 13.sp)
            }
        }
    }
}

// ── Fila de datos ─────────────────────────────────────────────────────────────
@Composable
private fun FilaNota(
    idx        : Int,
    f          : GetListDto,
    cargando   : Boolean,
    onEditar   : () -> Unit,
    onFirmar   : () -> Unit,
    onXml      : () -> Unit,
    onPdf      : () -> Unit,
    onImprimir : () -> Unit
) {
    val bg             = if (idx % 2 == 0) ColorFilaPar else ColorFilaImpar
    var comboExpanded by remember { mutableStateOf(false) }

    Row(
        modifier          = Modifier.background(bg).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CeldaDato(tipoVentaLabel(f.tipoVenta),             COLUMNAS[0].ancho)
        CeldaDato(ambienteLabel(f.ambiente),               COLUMNAS[1].ancho)
        CeldaDato(estadoDocumentoLabel(f.estadoDocumento), COLUMNAS[2].ancho)
        CeldaDato(f.serie ?: "—",                          COLUMNAS[3].ancho)
        CeldaDato(f.secuencial ?: "—",                     COLUMNAS[4].ancho)
        CeldaDato(f.numeroAutorizacion ?: "—",             COLUMNAS[5].ancho)
        CeldaDato(f.fechaEmision ?: "—",                   COLUMNAS[6].ancho)
        CeldaDato(f.numeroIdentificacion ?: "—",           COLUMNAS[7].ancho)
        CeldaDato(f.terceroNombre ?: "—",                  COLUMNAS[8].ancho)
        CeldaDato(f.email ?: "—",                          COLUMNAS[9].ancho)
        CeldaDato(estadoEmailLabel(f.emailEstado),         COLUMNAS[10].ancho)
        CeldaDato(fmtDecimal(f.subtotal),                  COLUMNAS[11].ancho, TextAlign.End)
        CeldaDato(fmtDecimal(f.totalDescuento),            COLUMNAS[12].ancho, TextAlign.End)
        CeldaDato(baseIva(f.valores, "0"),                 COLUMNAS[13].ancho, TextAlign.End)
        CeldaDato(baseIva(f.valores, "6"),                 COLUMNAS[14].ancho, TextAlign.End)
        CeldaDato(baseIva(f.valores, "7"),                 COLUMNAS[15].ancho, TextAlign.End)
        CeldaDato(baseIva(f.valores, "5"),                 COLUMNAS[16].ancho, TextAlign.End)
        CeldaDato(baseIva(f.valores, "8"),                 COLUMNAS[17].ancho, TextAlign.End)
        CeldaDato(baseIva(f.valores, "4"),                 COLUMNAS[18].ancho, TextAlign.End)
        CeldaDato(fmtDecimal(f.total),                     COLUMNAS[19].ancho, TextAlign.End)

        // ── Acciones
        Box(modifier = Modifier.width(COLUMNAS[20].ancho), contentAlignment = Alignment.Center) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEditar, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, "Editar", tint = ColorHeader, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onFirmar, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Lock, "Firma electrónica", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                }
                Box {
                    if (cargando) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(20.dp).padding(2.dp),
                            strokeWidth = 2.dp,
                            color       = ColorHeader
                        )
                    } else {
                        IconButton(
                            onClick  = { comboExpanded = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Default.ArrowDropDown,
                                contentDescription = "Descargar",
                                tint               = Color.White,
                                modifier           = Modifier
                                    .size(20.dp)
                                    .background(ColorHeader, shape = RoundedCornerShape(4.dp))
                            )
                        }
                    }
                    DropdownMenu(
                        expanded         = comboExpanded,
                        onDismissRequest = { comboExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text    = { Text("XML", fontWeight = FontWeight.Medium) },
                            onClick = { comboExpanded = false; onXml() }
                        )
                        DropdownMenuItem(
                            text    = { Text("PDF", fontWeight = FontWeight.Medium) },
                            onClick = { comboExpanded = false; onPdf() }
                        )
                        DropdownMenuItem(
                            text    = { Text("Imprimir", fontWeight = FontWeight.Medium) },
                            onClick = { comboExpanded = false; onImprimir() }
                        )
                    }
                }
            }
        }
    }
}

// ── Diálogo Firma Electrónica ─────────────────────────────────────────────────
@Composable
private fun DialogoFirma(
    serie: String, secuencial: String,
    firmando: Boolean, firmaError: String?,
    onProcesar: () -> Unit, onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!firmando) onCancelar() },
        title = {
            Text("Firma Electrónica", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text       = "Número: $serie-$secuencial",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = ColorTexto
                )
                HorizontalDivider(color = ColorBorde)
                Text(
                    text     = "Está a punto de realizar la firma electrónica del documento.",
                    fontSize = 13.sp,
                    color    = ColorSub
                )
                if (firmaError != null) {
                    Text(firmaError, color = Color(0xFFB00020), fontSize = 12.sp)
                }
                if (firmando) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = ColorHeader)
                        Text("Procesando firma...", fontSize = 12.sp, color = ColorSub)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick  = onProcesar,
                enabled  = !firmando,
                colors   = ButtonDefaults.buttonColors(containerColor = ColorHeader)
            ) { Text("Procesar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar, enabled = !firmando) { Text("Cancelar") }
        }
    )
}

// ── Helpers celda ─────────────────────────────────────────────────────────────
@Composable
private fun CeldaHeader(texto: String, ancho: Dp) {
    Text(
        text       = texto,
        color      = Color.White,
        fontSize   = 12.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines   = 1,
        overflow   = TextOverflow.Ellipsis,
        modifier   = Modifier.width(ancho).padding(horizontal = 8.dp)
    )
}

@Composable
private fun CeldaDato(
    texto: String,
    ancho: Dp,
    align: TextAlign = TextAlign.Start
) {
    Text(
        text      = texto,
        color     = ColorTexto,
        fontSize  = 12.sp,
        maxLines  = 1,
        overflow  = TextOverflow.Ellipsis,
        textAlign = align,
        modifier  = Modifier.width(ancho).padding(horizontal = 8.dp)
    )
}

// ── Paginador ─────────────────────────────────────────────────────────────────
@Composable
private fun Paginador(
    currentPage: Int,
    totalPages:  Int,
    totalItems:  Long,
    onAnterior:  () -> Unit,
    onSiguiente: () -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text     = "Total: $totalItems registro(s)  |  Página ${currentPage + 1} de $totalPages",
            fontSize = 12.sp,
            color    = ColorSub
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onAnterior, enabled = currentPage > 0) {
                Text("← Anterior", fontSize = 12.sp)
            }
            OutlinedButton(onClick = onSiguiente, enabled = currentPage < totalPages - 1) {
                Text("Siguiente →", fontSize = 12.sp)
            }
        }
    }
}

// ── Máscara visual dd/MM/yyyy ─────────────────────────────────────────────────
private object DateMaskTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val display = buildString {
            for (i in 0..7) {
                if (i == 2 || i == 4) append('/')
                append(if (i < digits.length) digits[i] else '_')
            }
        }
        val offsetMap = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = when {
                offset <= 2 -> offset
                offset <= 4 -> offset + 1
                else        -> offset + 2
            }
            override fun transformedToOriginal(offset: Int) = when {
                offset <= 2 -> offset
                offset <= 5 -> offset - 1
                else        -> offset - 2
            }.coerceIn(0, digits.length)
        }
        return TransformedText(AnnotatedString(display), offsetMap)
    }
}

// ── Barra de filtros ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltrosBar(state: NotasCreditoUiState, viewModel: NotasCreditoViewModel) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor   = ColorHeader,
        unfocusedBorderColor = ColorBorde,
        focusedLabelColor    = ColorHeader
    )

    Surface(
        color           = Color.White,
        shadowElevation = 1.dp,
        shape           = RoundedCornerShape(8.dp),
        modifier        = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {

            // ── Fila 1: Serie · Secuencial · Fecha Desde · Fecha Hasta
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value         = state.filterSerie,
                    onValueChange = viewModel::setFilterSerie,
                    label         = { Text("Serie", fontSize = 12.sp) },
                    singleLine    = true,
                    modifier      = Modifier.width(120.dp),
                    textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                    colors        = fieldColors
                )
                OutlinedTextField(
                    value         = state.filterSecuencial,
                    onValueChange = viewModel::setFilterSecuencial,
                    label         = { Text("Secuencial", fontSize = 12.sp) },
                    singleLine    = true,
                    modifier      = Modifier.width(130.dp),
                    textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                    colors        = fieldColors
                )
                OutlinedTextField(
                    value                = state.filterFechaDesde,
                    onValueChange        = { viewModel.setFilterFechaDesde(it.filter(Char::isDigit).take(8)) },
                    label                = { Text("Fecha Desde", fontSize = 12.sp) },
                    singleLine           = true,
                    modifier             = Modifier.width(140.dp),
                    textStyle            = LocalTextStyle.current.copy(fontSize = 13.sp),
                    visualTransformation = DateMaskTransformation,
                    colors               = fieldColors
                )
                OutlinedTextField(
                    value                = state.filterFechaHasta,
                    onValueChange        = { viewModel.setFilterFechaHasta(it.filter(Char::isDigit).take(8)) },
                    label                = { Text("Fecha Hasta", fontSize = 12.sp) },
                    singleLine           = true,
                    modifier             = Modifier.width(140.dp),
                    textStyle            = LocalTextStyle.current.copy(fontSize = 13.sp),
                    visualTransformation = DateMaskTransformation,
                    colors               = fieldColors
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Fila 2: Estado · Tercero (live-search) · Buscar · Limpiar
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.Bottom
            ) {
                // Dropdown Estado
                var expandedEstado by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded         = expandedEstado,
                    onExpandedChange = { expandedEstado = !expandedEstado },
                    modifier         = Modifier.width(160.dp)
                ) {
                    OutlinedTextField(
                        value         = state.filterEstado.label,
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Estado", fontSize = 12.sp) },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expandedEstado) },
                        modifier      = Modifier.menuAnchor().fillMaxWidth(),
                        textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                        colors        = fieldColors
                    )
                    ExposedDropdownMenu(
                        expanded         = expandedEstado,
                        onDismissRequest = { expandedEstado = false }
                    ) {
                        EstadoFiltroNotaCredito.entries.forEach { opcion ->
                            DropdownMenuItem(
                                text    = { Text(opcion.label, fontSize = 13.sp) },
                                onClick = { viewModel.setFilterEstado(opcion); expandedEstado = false }
                            )
                        }
                    }
                }

                Box {
                    OutlinedTextField(
                        value         = state.filterTercero,
                        onValueChange = viewModel::setFilterTercero,
                        label         = { Text("Tercero", fontSize = 12.sp) },
                        singleLine    = true,
                        modifier      = Modifier.width(160.dp),
                        textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp),
                        colors        = fieldColors,
                        trailingIcon  = {
                            if (state.buscandoTercero)
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            else if (state.filterTercero.isNotBlank())
                                IconButton(
                                    onClick  = { viewModel.setFilterTercero("") },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpiar", modifier = Modifier.size(14.dp))
                                }
                        }
                    )

                    DropdownMenu(
                        expanded         = state.terceroDropdownVisible,
                        onDismissRequest = viewModel::cerrarDropdownTercero,
                        modifier         = Modifier.width(260.dp).heightIn(max = 220.dp)
                    ) {
                        state.terceroSugerencias.forEach { t ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(t.tercero ?: "—", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        Text(t.numeroIdentificacion ?: "—", fontSize = 11.sp, color = ColorSub)
                                    }
                                },
                                onClick = { viewModel.seleccionarTercero(t) }
                            )
                        }
                    }
                }

                Spacer(Modifier.width(4.dp))

                Button(
                    onClick = viewModel::buscar,
                    colors  = ButtonDefaults.buttonColors(containerColor = ColorHeader)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Buscar", fontSize = 13.sp)
                }

                OutlinedButton(onClick = viewModel::limpiarFiltros) {
                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Limpiar", fontSize = 13.sp)
                }
            }
        }
    }
}

// ── Utils ─────────────────────────────────────────────────────────────────────
private fun fmtDecimal(valor: BigDecimal?): String =
    "%.2f".format(valor ?: BigDecimal.ZERO)

private fun baseIvaBD(valores: List<ResponseValoresDto>?, codigoPorcentaje: String): BigDecimal =
    valores
        ?.filter { it.codigo == "2" && it.codigoPorcentaje == codigoPorcentaje }
        ?.mapNotNull { it.baseImponible }
        ?.fold(BigDecimal.ZERO, BigDecimal::add)
        ?: BigDecimal.ZERO

private fun baseIva(valores: List<ResponseValoresDto>?, codigoPorcentaje: String): String =
    fmtDecimal(baseIvaBD(valores, codigoPorcentaje))

private fun tipoVentaLabel(tipoVenta: Any?): String = when (tipoVenta?.toString()) {
    "FAC" -> "FACTURA"
    "NDB" -> "NOTA DE DEBITO"
    "NCR" -> "NOTA DE CREDITO"
    "GRM" -> "GUIA DE REMISION"
    else  -> tipoVenta?.toString() ?: "—"
}

private fun ambienteLabel(ambiente: Int?): String = when (ambiente) {
    1    -> "Pruebas"
    2    -> "Producción"
    else -> ambiente?.toString() ?: "—"
}

private fun estadoDocumentoLabel(estado: Any?): String = when (estado?.toString()) {
    "PEN" -> "PENDIENTE"
    "ENV" -> "ENVIAR"
    "REC" -> "RECIBIDA"
    "AUT" -> "AUTORIZADA"
    "DEV" -> "DEVUELTA"
    "NOA" -> "NO AUTORIZADA"
    else  -> estado?.toString() ?: "—"
}

private fun estadoEmailLabel(emailEstado: Any?): String = when (emailEstado?.toString()) {
    "1"  -> "NO ENTREGADO"
    "2"  -> "ENTREGADO"
    "3"  -> "REBOTADO"
    else -> emailEstado?.toString() ?: "—"
}
