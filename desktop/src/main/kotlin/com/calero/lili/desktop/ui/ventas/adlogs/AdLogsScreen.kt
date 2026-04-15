package com.calero.lili.desktop.ui.ventas.adlogs

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calero.lili.core.adLogs.dto.AdLogsDtoResponse

// ── Colores ───────────────────────────────────────────────────────────────────
private val ColorHeader    = Color(0xFF1565C0)
private val ColorFondo     = Color(0xFFF0F4FF)
private val ColorBorde     = Color(0xFFDDE3F0)
private val ColorFilaPar   = Color(0xFFF5F7FF)
private val ColorFilaImpar = Color.White
private val ColorTexto     = Color(0xFF1A1A2E)
private val ColorSub       = Color(0xFF6B7A99)
private val ColorError     = Color(0xFFD32F2F)
private val ColorOk        = Color(0xFF2E7D32)

// ── Columnas ──────────────────────────────────────────────────────────────────
private data class ColLog(val titulo: String, val ancho: Dp)

private val COLS_LOG = listOf(
    ColLog("Serie",          90.dp),
    ColLog("Secuencial",    110.dp),
    ColLog("Tipo Documento",140.dp),
    ColLog("Mensaje",       350.dp),
    ColLog("Fecha",         150.dp),
    ColLog("Tipo",          110.dp)
)

// ── Máscara de fecha dd/MM/yyyy ───────────────────────────────────────────────
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
            override fun originalToTransformed(offset: Int): Int = when {
                offset <= 2 -> offset
                offset <= 4 -> offset + 1
                else        -> offset + 2
            }
            override fun transformedToOriginal(offset: Int): Int {
                val raw = when {
                    offset <= 2 -> offset
                    offset <= 5 -> offset - 1
                    else        -> offset - 2
                }
                return raw.coerceIn(0, digits.length)
            }
        }
        return TransformedText(AnnotatedString(display), offsetMap)
    }
}

// ── Screen ────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdLogsScreen(viewModel: AdLogsViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(20.dp)
    ) {

        // ── Encabezado ────────────────────────────────────────────────────────
        Column {
            Text(
                "Registros de Envío Autorización",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = ColorTexto
            )
            Text(
                "Historial de envíos al servicio de autorización del SRI",
                fontSize = 13.sp,
                color    = ColorSub
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── Barra de filtros ──────────────────────────────────────────────────
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value         = state.filterFechaDesde,
                onValueChange = { viewModel.setFilterFechaDesde(it.filter(Char::isDigit).take(8)) },
                label         = { Text("Fecha Desde", fontSize = 12.sp) },
                placeholder   = { Text("dd/mm/aaaa", fontSize = 12.sp, color = ColorSub) },
                visualTransformation = DateMaskTransformation,
                singleLine    = true,
                modifier      = Modifier.width(160.dp),
                textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp)
            )
            OutlinedTextField(
                value         = state.filterFechaHasta,
                onValueChange = { viewModel.setFilterFechaHasta(it.filter(Char::isDigit).take(8)) },
                label         = { Text("Fecha Hasta", fontSize = 12.sp) },
                placeholder   = { Text("dd/mm/aaaa", fontSize = 12.sp, color = ColorSub) },
                visualTransformation = DateMaskTransformation,
                singleLine    = true,
                modifier      = Modifier.width(160.dp),
                textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp)
            )

            // ── Combo Tipo ────────────────────────────────────────────────────
            var expandedTipo by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded         = expandedTipo,
                onExpandedChange = { expandedTipo = !expandedTipo },
                modifier         = Modifier.width(160.dp)
            ) {
                OutlinedTextField(
                    value         = state.filterTipo.label,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Tipo", fontSize = 12.sp) },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expandedTipo) },
                    modifier      = Modifier.menuAnchor().fillMaxWidth(),
                    textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp)
                )
                ExposedDropdownMenu(
                    expanded         = expandedTipo,
                    onDismissRequest = { expandedTipo = false }
                ) {
                    TipoFiltroLog.entries.forEach { opcion ->
                        DropdownMenuItem(
                            text    = { Text(opcion.label, fontSize = 13.sp) },
                            onClick = { viewModel.setFilterTipo(opcion); expandedTipo = false }
                        )
                    }
                }
            }

            Button(
                onClick  = viewModel::buscar,
                enabled  = !state.isLoading,
                colors   = ButtonDefaults.buttonColors(containerColor = ColorHeader)
            ) { Text("Buscar", fontSize = 13.sp) }

            TextButton(onClick = viewModel::limpiarFiltros) {
                Text("Limpiar", fontSize = 13.sp, color = ColorSub)
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Banner de error ───────────────────────────────────────────────────
        state.errorMessage?.let { err ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(err, fontSize = 13.sp, color = ColorError)
                    TextButton(onClick = viewModel::dismissError) {
                        Text("Cerrar", fontSize = 12.sp, color = ColorError)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // ── Tabla ─────────────────────────────────────────────────────────────
        Surface(
            modifier        = Modifier.weight(1f).fillMaxWidth(),
            shadowElevation = 2.dp,
            color           = Color.White,
            shape           = MaterialTheme.shapes.small
        ) {
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ColorHeader, strokeWidth = 3.dp)
                    }
                }
                state.logs.isEmpty() && state.paginator == null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Ingrese un rango de fechas y presione Buscar",
                            color    = ColorSub,
                            fontSize = 14.sp
                        )
                    }
                }
                state.logs.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No se encontraron registros para el rango de fechas indicado",
                            color    = ColorSub,
                            fontSize = 14.sp
                        )
                    }
                }
                else -> {
                    val scrollH    = rememberScrollState()
                    val scrollV    = rememberScrollState()
                    val anchoTotal = COLS_LOG.fold(0.dp) { acc, col -> acc + col.ancho }

                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 12.dp, end = 12.dp)
                        ) {
                            // Cabecera fija
                            Box(modifier = Modifier.horizontalScroll(scrollH)) {
                                Row(
                                    modifier = Modifier
                                        .width(anchoTotal)
                                        .background(ColorHeader)
                                        .padding(vertical = 10.dp)
                                ) {
                                    COLS_LOG.forEach { col ->
                                        CeldaHeaderLog(col.titulo, col.ancho)
                                    }
                                }
                            }
                            HorizontalDivider(color = ColorBorde, thickness = 1.dp)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(scrollH)
                                    .verticalScroll(scrollV)
                            ) {
                                Column(modifier = Modifier.width(anchoTotal)) {
                                    state.logs.forEachIndexed { idx, log ->
                                        FilaLog(idx = idx, log = log)
                                        HorizontalDivider(color = ColorBorde, thickness = 0.5.dp)
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
        }

        // ── Paginador ─────────────────────────────────────────────────────────
        Spacer(Modifier.height(10.dp))
        state.paginator?.let { pag ->
            PaginadorLogs(
                paginaActual  = state.currentPage,
                totalPaginas  = pag.totalPages ?: 0,
                totalElementos = pag.totalElements ?: 0,
                isLoading     = state.isLoading,
                onAnterior    = { if (state.currentPage > 0) viewModel.irPagina(state.currentPage - 1) },
                onSiguiente   = { if (state.currentPage < (pag.totalPages ?: 1) - 1) viewModel.irPagina(state.currentPage + 1) }
            )
        }
    }
}

// ── Fila ──────────────────────────────────────────────────────────────────────
@Composable
private fun FilaLog(idx: Int, log: AdLogsDtoResponse) {
    val bg = if (idx % 2 == 0) ColorFilaPar else ColorFilaImpar
    Row(
        modifier          = Modifier.background(bg).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CeldaDatoLog(log.serie       ?: "—", COLS_LOG[0].ancho)
        CeldaDatoLog(log.secuencial  ?: "—", COLS_LOG[1].ancho)
        CeldaDatoLog(log.tipoDocumento ?: "—", COLS_LOG[2].ancho)
        CeldaDatoLog(log.mensajes    ?: "—", COLS_LOG[3].ancho)
        CeldaDatoLog(log.fechaHora   ?: "—", COLS_LOG[4].ancho)
        // Columna Tipo con color
        val tipoLabel = when (log.tipo) {
            "I"  -> "Informativo"
            "E"  -> "Error"
            else -> log.tipo ?: "—"
        }
        val tipoColor = when (log.tipo) {
            "I"  -> ColorOk
            "E"  -> ColorError
            else -> ColorTexto
        }
        Text(
            text      = tipoLabel,
            color     = tipoColor,
            fontSize  = 12.sp,
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis,
            fontWeight = FontWeight.SemiBold,
            modifier  = Modifier.width(COLS_LOG[5].ancho).padding(horizontal = 8.dp)
        )
    }
}

// ── Celdas ────────────────────────────────────────────────────────────────────
@Composable
private fun CeldaHeaderLog(texto: String, ancho: Dp) {
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
private fun CeldaDatoLog(texto: String, ancho: Dp) {
    Text(
        text     = texto,
        color    = ColorTexto,
        fontSize = 12.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.width(ancho).padding(horizontal = 8.dp)
    )
}

// ── Paginador ─────────────────────────────────────────────────────────────────
@Composable
private fun PaginadorLogs(
    paginaActual: Int,
    totalPaginas: Int,
    totalElementos: Long,
    isLoading: Boolean,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text     = "Total: $totalElementos registro(s)",
            fontSize = 12.sp,
            color    = ColorSub
        )
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick  = onAnterior,
                enabled  = !isLoading && paginaActual > 0,
                modifier = Modifier.height(32.dp)
            ) { Text("Anterior", fontSize = 12.sp) }

            Text(
                text      = "Página ${paginaActual + 1} de $totalPaginas",
                fontSize  = 12.sp,
                color     = ColorTexto,
                textAlign = TextAlign.Center,
                modifier  = Modifier.widthIn(min = 100.dp)
            )

            OutlinedButton(
                onClick  = onSiguiente,
                enabled  = !isLoading && paginaActual < totalPaginas - 1,
                modifier = Modifier.height(32.dp)
            ) { Text("Siguiente", fontSize = 12.sp) }
        }
    }
}
