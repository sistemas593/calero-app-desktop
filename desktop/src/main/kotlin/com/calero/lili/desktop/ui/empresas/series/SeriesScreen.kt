package com.calero.lili.desktop.ui.empresas.series

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.calero.lili.core.modAdminEmpresasSeries.dto.AdEmpresaSerieGetListDto
import java.util.UUID

private val COL_ACCIONES  = 70.dp
private val COL_SERIE     = 120.dp
private val COL_NOMBRE    = 220.dp
private val COL_DIRECCION = 260.dp

private val ColorHeader   = Color(0xFF1565C0)
private val ColorRowPar   = Color(0xFFF5F7FF)
private val ColorRowImpar = Color.White
private val ColorBorde    = Color(0xFFDDE3F0)
private val ColorTexto    = Color(0xFF1A1A2E)
private val ColorTextoSub = Color(0xFF555577)

@Composable
fun SeriesScreen(
    viewModel: SeriesViewModel,
    onNuevaSerie: () -> Unit,
    onEditarSerie: (UUID) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4FF))
            .padding(20.dp)
    ) {
        // ── Título
        Text(
            text = "Series de Empresa",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // ── Barra de filtros
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state.filterText,
                onValueChange = viewModel::setFilter,
                label = { Text("Buscar (Serie / Nombre Comercial)") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Color(0xFF1565C0),
                    unfocusedBorderColor = ColorBorde
                )
            )
            Button(
                onClick  = viewModel::buscar,
                enabled  = !state.isLoading,
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color       = Color.White
                    )
                } else {
                    Text("Buscar")
                }
            }
        }

        // ── Botón Nueva Serie
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onNuevaSerie,
                colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
            ) {
                Text("+ Nueva Serie")
            }
        }

        Spacer(Modifier.height(4.dp))

        // ── Diálogo de error
        state.errorMessage?.let { msg ->
            AlertDialog(
                onDismissRequest = viewModel::dismissError,
                icon             = { Text("⚠️", fontSize = 28.sp) },
                title            = { Text("Error", fontWeight = FontWeight.Bold) },
                text             = { Text(msg) },
                confirmButton    = {
                    Button(
                        onClick = viewModel::dismissError,
                        colors  = ButtonDefaults.buttonColors(containerColor = ColorHeader)
                    ) { Text("Aceptar") }
                }
            )
        }

        // ── Tabla
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.series.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron series.", color = ColorTextoSub)
                }
            }
            else -> {
                Surface(
                    modifier        = Modifier.weight(1f).fillMaxWidth(),
                    shape           = MaterialTheme.shapes.medium,
                    shadowElevation = 2.dp,
                    color           = Color.White
                ) {
                    Column {
                        TablaSeries(series = state.series, onEditar = onEditarSerie)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Paginación
        state.paginator?.let { pag ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text     = "Total: ${pag.totalElements ?: 0L} registro(s)",
                    color    = ColorTextoSub,
                    fontSize = 13.sp
                )
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick        = viewModel::paginaAnterior,
                        enabled        = !(pag.first ?: true),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) { Text("← Anterior") }

                    Text(
                        text       = "Página ${state.currentPage + 1} de ${pag.totalPages}",
                        fontSize   = 13.sp,
                        color      = ColorTexto,
                        fontWeight = FontWeight.Medium
                    )

                    OutlinedButton(
                        onClick        = viewModel::paginaSiguiente,
                        enabled        = !(pag.last ?: true),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) { Text("Siguiente →") }
                }
            }
        }
    }
}

@Composable
private fun TablaSeries(
    series: List<AdEmpresaSerieGetListDto>,
    onEditar: (UUID) -> Unit
) {
    Column {
        // Encabezado
        Row(
            modifier = Modifier
                .background(ColorHeader)
                .padding(vertical = 10.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CeldaHeader("Serie",            COL_SERIE,     TextAlign.Start)
            CeldaHeader("Nombre Comercial", COL_NOMBRE,    TextAlign.Start)
            CeldaHeader("Dirección",        COL_DIRECCION, TextAlign.Start)
            CeldaHeader("Acciones",         COL_ACCIONES,  TextAlign.Center)
        }
        HorizontalDivider(color = ColorBorde)

        LazyColumn {
            itemsIndexed(series) { idx, serie ->
                Row(
                    modifier = Modifier
                        .background(if (idx % 2 == 0) ColorRowPar else ColorRowImpar)
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CeldaDato(serie.serie ?: "-",                     COL_SERIE,     TextAlign.Start)
                    CeldaDato(serie.nombreComercial ?: "-",           COL_NOMBRE,    TextAlign.Start)
                    CeldaDato(serie.direccionEstablecimiento ?: "-",  COL_DIRECCION, TextAlign.Start)
                    Box(
                        modifier         = Modifier.width(COL_ACCIONES).height(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick  = { onEditar(serie.idSerie) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint               = Color(0xFF1565C0),
                                modifier           = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                HorizontalDivider(color = ColorBorde, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun CeldaHeader(texto: String, ancho: Dp, align: TextAlign) {
    Text(
        text       = texto,
        color      = Color.White,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 13.sp,
        textAlign  = align,
        maxLines   = 1,
        overflow   = TextOverflow.Ellipsis,
        modifier   = Modifier.width(ancho).padding(end = 8.dp)
    )
}

@Composable
private fun CeldaDato(texto: String, ancho: Dp, align: TextAlign) {
    Text(
        text      = texto,
        color     = ColorTexto,
        fontSize  = 13.sp,
        textAlign = align,
        maxLines  = 1,
        overflow  = TextOverflow.Ellipsis,
        modifier  = Modifier.width(ancho).padding(end = 8.dp)
    )
}
