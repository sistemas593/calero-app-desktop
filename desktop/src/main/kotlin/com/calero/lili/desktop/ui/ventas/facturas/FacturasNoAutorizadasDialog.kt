package com.calero.lili.desktop.ui.ventas.facturas

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.calero.lili.core.modVentas.VtVentaEntity

// ── Colores internos ──────────────────────────────────────────────────────────
private val ColorHeaderDialog = Color(0xFF1565C0)
private val ColorRojo         = Color(0xFFD32F2F)
private val ColorFilaPar      = Color(0xFFF5F7FF)
private val ColorFilaImpar    = Color.White
private val ColorBorde        = Color(0xFFDDE3F0)
private val ColorSub          = Color(0xFF6B7A99)

// ── Definición de columnas ────────────────────────────────────────────────────
private data class Col(val titulo: String, val ancho: Dp)

private val COLS = listOf(
    Col("Serie",           80.dp),
    Col("Secuencial",     100.dp),
    Col("Fecha Emisión",  110.dp),
    Col("Cliente",        220.dp),
    Col("N. Identificación", 130.dp),
    Col("Total",          100.dp),
    Col("Estado",          90.dp)
)

@Composable
fun FacturasNoAutorizadasDialog(
    facturas     : List<VtVentaEntity>,
    isLoading    : Boolean,
    isProcesando : Boolean,
    onProcesar   : () -> Unit,
    onCerrar     : () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier       = Modifier.fillMaxSize(0.92f),
            shape          = MaterialTheme.shapes.large,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Barra superior ────────────────────────────────────────────
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Facturas No Autorizadas",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = ColorHeaderDialog
                        )
                        Text(
                            "Comprobantes con estado ENV pendientes de autorización",
                            fontSize = 12.sp,
                            color    = ColorSub
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick  = onProcesar,
                            enabled  = !isLoading && !isProcesando && facturas.isNotEmpty(),
                            colors   = ButtonDefaults.buttonColors(containerColor = ColorHeaderDialog)
                        ) {
                            if (isProcesando) {
                                CircularProgressIndicator(
                                    modifier  = Modifier.size(14.dp),
                                    color     = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(6.dp))
                            }
                            Text("Procesar facturas", fontSize = 13.sp)
                        }
                        Button(
                            onClick = onCerrar,
                            colors  = ButtonDefaults.buttonColors(containerColor = ColorHeaderDialog)
                        ) { Text("Cerrar", fontSize = 13.sp) }
                    }
                }

                HorizontalDivider()

                // ── Contenido ─────────────────────────────────────────────────
                Box(
                    modifier        = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = ColorHeaderDialog)
                            Spacer(Modifier.height(8.dp))
                            Text("Cargando facturas…", fontSize = 13.sp, color = ColorSub)
                        }

                        facturas.isEmpty() -> Text(
                            "No hay facturas pendientes de autorización",
                            fontSize = 14.sp,
                            color    = ColorSub
                        )

                        else -> TablaFacturasNoAutorizadas(facturas)
                    }
                }
            }
        }
    }
}

// ── Tabla de facturas ─────────────────────────────────────────────────────────
@Composable
private fun TablaFacturasNoAutorizadas(facturas: List<VtVentaEntity>) {
    val scrollH = rememberScrollState()
    val scrollV = rememberScrollState()

    val totalAncho = COLS.fold(0.dp) { acc, col -> acc + col.ancho } + 2.dp

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Cabecera fija (solo scroll horizontal)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollH)
                    .background(Color(0xFFE8EDF8))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
            ) {
                COLS.forEach { col ->
                    Text(
                        text       = col.titulo,
                        modifier   = Modifier.width(col.ancho),
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color(0xFF1A1A2E),
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = ColorBorde)

            // Filas con scroll vertical y horizontal sincronizado
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .horizontalScroll(scrollH)
                    .verticalScroll(scrollV)
            ) {
                Column(modifier = Modifier.width(totalAncho)) {
                    facturas.forEachIndexed { idx, entity ->
                        val bg = if (idx % 2 == 0) ColorFilaImpar else ColorFilaPar
                        FilaFactura(entity, bg)
                        HorizontalDivider(color = ColorBorde, thickness = 0.5.dp)
                    }
                }
            }
        }

        // Contador inferior
        Text(
            text     = "${facturas.size} factura(s) encontrada(s)",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 8.dp),
            fontSize = 12.sp,
            color    = ColorSub
        )
    }
}

// ── Fila individual ───────────────────────────────────────────────────────────
@Composable
private fun FilaFactura(entity: VtVentaEntity, bg: Color) {
    Row(
        modifier          = Modifier
            .background(bg)
            .padding(vertical = 7.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CeldaTexto(entity.serie ?: "",                                COLS[0].ancho)
        CeldaTexto(entity.secuencial ?: "",                           COLS[1].ancho)
        CeldaTexto(entity.fechaEmision?.toString() ?: "",             COLS[2].ancho)
        CeldaTexto(entity.tercero?.tercero ?: "—",                    COLS[3].ancho)
        CeldaTexto(entity.tercero?.numeroIdentificacion ?: "—",       COLS[4].ancho)
        CeldaTexto(entity.total?.setScale(2)?.toPlainString() ?: "0.00", COLS[5].ancho, align = TextAlign.End)
        CeldaTexto(entity.estadoDocumento?.name ?: "",                COLS[6].ancho)
    }
}

// ── Celda de texto reutilizable ───────────────────────────────────────────────
@Composable
private fun CeldaTexto(
    valor  : String,
    ancho  : Dp,
    align  : TextAlign = TextAlign.Start
) {
    Text(
        text      = valor,
        modifier  = Modifier.width(ancho),
        fontSize  = 12.sp,
        color     = Color(0xFF1A1A2E),
        maxLines  = 1,
        overflow  = TextOverflow.Ellipsis,
        textAlign = align
    )
}
