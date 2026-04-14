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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calero.lili.core.modVentas.VtVentaEntity
import java.math.BigDecimal

// ── Colores (idénticos a FacturasScreen) ─────────────────────────────────────
private val ColorHeader    = Color(0xFF1565C0)
private val ColorFondo     = Color(0xFFF0F4FF)
private val ColorBorde     = Color(0xFFDDE3F0)
private val ColorFilaPar   = Color(0xFFF5F7FF)
private val ColorFilaImpar = Color.White
private val ColorTexto     = Color(0xFF1A1A2E)
private val ColorSub       = Color(0xFF6B7A99)
private val ColorOk        = Color(0xFF2E7D32)
private val ColorError     = Color(0xFFD32F2F)

// ── Columnas ──────────────────────────────────────────────────────────────────
private data class ColEA(val titulo: String, val ancho: Dp)

private val COLS_EA = listOf(
    ColEA("Tipo Documento",    120.dp),
    ColEA("Ambiente",           90.dp),
    ColEA("Serie",              80.dp),
    ColEA("Secuencial",        100.dp),
    ColEA("Fecha Emisión",     110.dp),
    ColEA("Cliente",           220.dp),
    ColEA("N. Identificación", 130.dp),
    ColEA("Total",             100.dp),
    ColEA("Estado",             90.dp)
)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun EnviarAAutorizarScreen(viewModel: EnviarAAutorizarViewModel) {

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(20.dp)
    ) {

        // ── Encabezado ────────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text("Enviar a Autorizar", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ColorTexto)
                Text("Comprobantes con estado ENV pendientes de autorización", fontSize = 13.sp, color = ColorSub)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = viewModel::cargar,
                    enabled = !state.isLoading && !state.isProcesando,
                    colors  = ButtonDefaults.buttonColors(containerColor = ColorHeader)
                ) { Text("Recargar", fontSize = 13.sp) }
                Button(
                    onClick = viewModel::procesarTodas,
                    enabled = !state.isLoading && !state.isProcesando && state.facturas.isNotEmpty(),
                    colors  = ButtonDefaults.buttonColors(containerColor = ColorHeader)
                ) {
                    if (state.isProcesando) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(14.dp),
                            color       = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                    Text("Procesar facturas", fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Banners de resultado / error ──────────────────────────────────────
        state.resultado?.let { msg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(msg, fontSize = 13.sp, color = ColorOk, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = viewModel::dismissResultado) {
                        Text("Cerrar", fontSize = 12.sp, color = ColorOk)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        state.error?.let { err ->
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
                    TextButton(onClick = viewModel::dismissResultado) {
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
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ColorHeader, strokeWidth = 3.dp)
                }
            } else {
                val scrollH    = rememberScrollState()
                val scrollV    = rememberScrollState()
                val anchoTotal = COLS_EA.fold(0.dp) { acc, col -> acc + col.ancho }

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
                                COLS_EA.forEachIndexed { idx, col ->
                                    CeldaHeaderEA(col.titulo, col.ancho, if (idx == 7) TextAlign.End else TextAlign.Start)
                                }
                            }
                        }
                        HorizontalDivider(color = ColorBorde, thickness = 1.dp)

                        if (state.facturas.isEmpty()) {
                            Box(
                                Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No hay facturas pendientes de autorización",
                                    color    = ColorSub,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(scrollH)
                                    .verticalScroll(scrollV)
                            ) {
                                Column(modifier = Modifier.width(anchoTotal)) {
                                    state.facturas.forEachIndexed { idx, entity ->
                                        FilaEnviarAutorizar(idx = idx, entity = entity)
                                        HorizontalDivider(color = ColorBorde, thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }

                    // Scrollbar horizontal
                    HorizontalScrollbar(
                        adapter  = rememberScrollbarAdapter(scrollH),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(end = 12.dp, bottom = 1.dp)
                    )

                    // Scrollbar vertical
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

        // ── Contador inferior ─────────────────────────────────────────────────
        Spacer(Modifier.height(8.dp))
        Text(
            text     = "Total: ${state.facturas.size} factura(s) encontrada(s)",
            fontSize = 12.sp,
            color    = ColorSub
        )
    }
}

// ── Fila ──────────────────────────────────────────────────────────────────────
@Composable
private fun FilaEnviarAutorizar(idx: Int, entity: VtVentaEntity) {
    val bg = if (idx % 2 == 0) ColorFilaPar else ColorFilaImpar
    Row(
        modifier          = Modifier.background(bg).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CeldaDatoEA(tipoVentaLabelEA(entity.tipoVenta),             COLS_EA[0].ancho)
        CeldaDatoEA(ambienteLabelEA(entity.ambiente),               COLS_EA[1].ancho)
        CeldaDatoEA(entity.serie ?: "—",                            COLS_EA[2].ancho)
        CeldaDatoEA(entity.secuencial ?: "—",                       COLS_EA[3].ancho)
        CeldaDatoEA(entity.fechaEmision?.toString() ?: "—",         COLS_EA[4].ancho)
        CeldaDatoEA(entity.tercero?.tercero ?: "—",                 COLS_EA[5].ancho)
        CeldaDatoEA(entity.tercero?.numeroIdentificacion ?: "—",    COLS_EA[6].ancho)
        CeldaDatoEA(fmtDecimalEA(entity.total),                     COLS_EA[7].ancho, TextAlign.End)
        CeldaDatoEA(entity.estadoDocumento?.name ?: "—",            COLS_EA[8].ancho)
    }
}

// ── Celdas ────────────────────────────────────────────────────────────────────
@Composable
private fun CeldaHeaderEA(texto: String, ancho: Dp, align: TextAlign = TextAlign.Start) {
    Text(
        text       = texto,
        color      = Color.White,
        fontSize   = 12.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines   = 1,
        overflow   = TextOverflow.Ellipsis,
        textAlign  = align,
        modifier   = Modifier.width(ancho).padding(horizontal = 8.dp)
    )
}

@Composable
private fun CeldaDatoEA(texto: String, ancho: Dp, align: TextAlign = TextAlign.Start) {
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

// ── Utils ─────────────────────────────────────────────────────────────────────
private fun fmtDecimalEA(valor: BigDecimal?): String =
    "%.2f".format(valor ?: BigDecimal.ZERO)

private fun tipoVentaLabelEA(tipoVenta: String?): String = when (tipoVenta) {
    "FAC" -> "FACTURA"
    "NDB" -> "NOTA DE DEBITO"
    "NCR" -> "NOTA DE CREDITO"
    "GRM" -> "GUIA DE REMISION"
    else  -> tipoVenta ?: "—"
}

private fun ambienteLabelEA(ambiente: Int?): String = when (ambiente) {
    1    -> "PRUEBAS"
    2    -> "PRODUCCION"
    else -> ambiente?.toString() ?: "—"
}
