package com.calero.lili.desktop.ui.ventas.facturas

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
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
    Columna("#",                  40.dp),
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
    Columna("Total",              100.dp)
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun FacturasScreen(viewModel: FacturasViewModel) {
    val state by viewModel.state.collectAsState()

    // Error dialog
    if (state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title            = { Text("Error") },
            text             = { Text(state.errorMessage ?: "") },
            confirmButton    = {
                TextButton(onClick = viewModel::dismissError) { Text("Aceptar") }
            }
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
                Text("Facturas", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ColorTexto)
                Text("Listado de ventas / facturas", fontSize = 13.sp, color = ColorSub)
            }
        }

        Spacer(Modifier.height(16.dp))

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
                // Ancho total fijo = suma exacta de todas las columnas
                val anchoTotal = COLUMNAS.fold(0.dp) { acc, col -> acc + col.ancho }

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 12.dp, end = 12.dp)
                    ) {
                        // ── Cabecera con ancho fijo y scrollH compartido
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

                        if (state.facturas.isEmpty()) {
                            Box(
                                Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No se encontraron facturas", color = ColorSub, fontSize = 14.sp)
                            }
                        } else {
                            // ── Cuerpo: mismo scrollH + scrollV, ancho fijo idéntico al header
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(scrollH)
                                    .verticalScroll(scrollV)
                            ) {
                                Column(modifier = Modifier.width(anchoTotal)) {
                                    state.facturas.forEachIndexed { idx, factura ->
                                        FilaFactura(idx, factura)
                                        HorizontalDivider(color = ColorBorde, thickness = 0.5.dp)
                                    }
                                }
                            }

                        }
                    }

                    // ── Barra horizontal
                    HorizontalScrollbar(
                        adapter  = rememberScrollbarAdapter(scrollH),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(end = 12.dp, bottom = 1.dp)
                    )

                    // ── Barra vertical
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
            PaginadorFacturas(
                currentPage  = state.currentPage,
                totalPages   = pag.totalPages ?: 1,
                totalItems   = pag.totalElements ?: 0,
                onAnterior   = { if (state.currentPage > 0) viewModel.irPagina(state.currentPage - 1) },
                onSiguiente  = { if (state.currentPage < (pag.totalPages ?: 1) - 1) viewModel.irPagina(state.currentPage + 1) }
            )
        }
    }
}

// ── Fila de datos ─────────────────────────────────────────────────────────────
@Composable
private fun FilaFactura(idx: Int, f: GetListDto) {
    val bg = if (idx % 2 == 0) ColorFilaPar else ColorFilaImpar

    Row(
        modifier          = Modifier.background(bg).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CeldaDato((idx + 1).toString(),             COLUMNAS[0].ancho,  TextAlign.Center)
        CeldaDato(tipoVentaLabel(f.tipoVenta),        COLUMNAS[1].ancho)
        CeldaDato(ambienteLabel(f.ambiente),         COLUMNAS[2].ancho)
        CeldaDato(estadoDocumentoLabel(f.estadoDocumento), COLUMNAS[3].ancho)
        CeldaDato(f.serie ?: "—",                   COLUMNAS[4].ancho)
        CeldaDato(f.secuencial ?: "—",              COLUMNAS[5].ancho)
        CeldaDato(f.numeroAutorizacion ?: "—",       COLUMNAS[6].ancho)
        CeldaDato(f.fechaEmision ?: "—",            COLUMNAS[7].ancho)
        CeldaDato(f.numeroIdentificacion ?: "—",     COLUMNAS[8].ancho)
        CeldaDato(f.terceroNombre ?: "—",           COLUMNAS[9].ancho)
        CeldaDato(f.email ?: "—",                   COLUMNAS[10].ancho)
        CeldaDato(estadoEmailLabel(f.emailEstado),   COLUMNAS[11].ancho)
        CeldaDato(fmtDecimal(f.subtotal),               COLUMNAS[12].ancho, TextAlign.End)
        CeldaDato(fmtDecimal(f.totalDescuento),          COLUMNAS[13].ancho, TextAlign.End)
        CeldaDato(baseIva(f.valores, "0"),               COLUMNAS[14].ancho, TextAlign.End)
        CeldaDato(baseIva(f.valores, "6"),               COLUMNAS[15].ancho, TextAlign.End)
        CeldaDato(baseIva(f.valores, "7"),               COLUMNAS[16].ancho, TextAlign.End)
        CeldaDato(baseIva(f.valores, "5"),               COLUMNAS[17].ancho, TextAlign.End)
        CeldaDato(baseIva(f.valores, "8"),               COLUMNAS[18].ancho, TextAlign.End)
        CeldaDato(baseIva(f.valores, "4"),               COLUMNAS[19].ancho, TextAlign.End)
        CeldaDato(fmtDecimal(f.total),                   COLUMNAS[20].ancho, TextAlign.End)
    }
}

// ── Helpers celda ─────────────────────────────────────────────────────────────
@Composable
private fun CeldaHeader(texto: String, ancho: Dp) {
    Text(
        text      = texto,
        color     = Color.White,
        fontSize  = 12.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines  = 1,
        overflow  = TextOverflow.Ellipsis,
        modifier  = Modifier.width(ancho).padding(horizontal = 8.dp)
    )
}

@Composable
private fun CeldaDato(
    texto:  String,
    ancho:  Dp,
    align:  TextAlign = TextAlign.Start
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
private fun PaginadorFacturas(
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
            OutlinedButton(
                onClick  = onAnterior,
                enabled  = currentPage > 0
            ) { Text("← Anterior", fontSize = 12.sp) }
            OutlinedButton(
                onClick  = onSiguiente,
                enabled  = currentPage < totalPages - 1
            ) { Text("Siguiente →", fontSize = 12.sp) }
        }
    }
}

// ── Utils ─────────────────────────────────────────────────────────────────────
/** Formatea un BigDecimal con 2 decimales; devuelve "0.00" si es nulo. */
private fun fmtDecimal(valor: BigDecimal?): String =
    "%.2f".format(valor ?: BigDecimal.ZERO)

/**
 * Suma las baseImponible de los valores cuyo codigo = "2" (IVA)
 * y cuyo codigoPorcentaje coincida con el indicado.
 *
 * Tabla porcentajes IVA:
 *  "0" → 0%  (Base Cero)
 *  "4" → 15% (Base Gravada 15%)
 *  "5" → 5%  (Base Gravada 5%)
 *  "6" → No Objeto de Impuesto
 *  "7" → Exento de IVA
 *  "8" → IVA Diferenciado (Base Gravada 8%)
 */
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
