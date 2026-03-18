package com.calero.lili.desktop.ui.terceros

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
import com.calero.lili.core.modTerceros.dto.GeTerceroGetListDto
import java.util.UUID

private val COL_NUM     = 50.dp
private val COL_NOMBRE  = 220.dp
private val COL_TIPOID  = 110.dp
private val COL_NUMID   = 140.dp
private val COL_TIPOS   = 200.dp
private val COL_ACCIONES = 70.dp

private val CColorHeader   = Color(0xFF1565C0)
private val CColorRowPar   = Color(0xFFF5F7FF)
private val CColorRowImpar = Color.White
private val CColorBorde    = Color(0xFFDDE3F0)
private val CColorTexto    = Color(0xFF1A1A2E)
private val CColorTextoSub = Color(0xFF555577)

@Composable
fun ClientesScreen(
    viewModel: ClientesViewModel,
    onNuevoCliente: () -> Unit,
    onEditarCliente: (UUID) -> Unit = {}
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
            text       = "Clientes",
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold,
            color      = CColorTexto,
            modifier   = Modifier.padding(bottom = 16.dp)
        )

        // ── Barra de filtros
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier              = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value         = state.filterText,
                onValueChange = viewModel::setFilter,
                label         = { Text("Buscar (Nombre / Número Identificación)") },
                singleLine    = true,
                modifier      = Modifier.weight(1f),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = CColorHeader,
                    unfocusedBorderColor = CColorBorde
                )
            )
            Button(
                onClick = viewModel::buscar,
                enabled = !state.isLoading,
                colors  = ButtonDefaults.buttonColors(containerColor = CColorHeader)
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

        // ── Botón Nuevo Cliente
        Row(
            modifier              = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onNuevoCliente,
                colors  = ButtonDefaults.buttonColors(containerColor = CColorHeader)
            ) { Text("+ Nuevo Cliente") }
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
                        colors  = ButtonDefaults.buttonColors(containerColor = CColorHeader)
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
            state.clientes.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron clientes.", color = CColorTextoSub)
                }
            }
            else -> {
                Surface(
                    modifier        = Modifier.weight(1f).fillMaxWidth(),
                    shape           = MaterialTheme.shapes.medium,
                    shadowElevation = 2.dp,
                    color           = Color.White
                ) {
                    TablaClientes(clientes = state.clientes, onEditar = onEditarCliente)
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
                    color    = CColorTextoSub,
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
                        color      = CColorTexto,
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
private fun TablaClientes(
    clientes: List<GeTerceroGetListDto>,
    onEditar: (UUID) -> Unit
) {
    Column {
        // Encabezado
        Row(
            modifier = Modifier
                .background(CColorHeader)
                .padding(vertical = 10.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CCeldaHeader("#",                  COL_NUM,      TextAlign.Center)
            CCeldaHeader("Nombre",             COL_NOMBRE,   TextAlign.Start)
            CCeldaHeader("Tipo ID",            COL_TIPOID,   TextAlign.Start)
            CCeldaHeader("Número ID",          COL_NUMID,    TextAlign.Start)
            CCeldaHeader("Tipos",              COL_TIPOS,    TextAlign.Start)
            CCeldaHeader("Acciones",           COL_ACCIONES, TextAlign.Center)
        }
        HorizontalDivider(color = CColorBorde)

        LazyColumn {
            itemsIndexed(clientes) { idx, cliente ->
                Row(
                    modifier = Modifier
                        .background(if (idx % 2 == 0) CColorRowPar else CColorRowImpar)
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CCeldaDato((idx + 1).toString(),           COL_NUM,      TextAlign.Center)
                    CCeldaDato(cliente.tercero ?: "-",         COL_NOMBRE,   TextAlign.Start)
                    CCeldaDato(cliente.tipoIdentificacion ?: "-", COL_TIPOID, TextAlign.Start)
                    CCeldaDato(cliente.numeroIdentificacion ?: "-", COL_NUMID, TextAlign.Start)
                    CCeldaDato(
                        texto   = cliente.tipoTerceros?.joinToString(" | ") { it.tipoTercero ?: "" } ?: "-",
                        ancho   = COL_TIPOS,
                        align   = TextAlign.Start
                    )
                    Box(
                        modifier         = Modifier.width(COL_ACCIONES).height(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick  = { onEditar(cliente.idTercero) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint               = CColorHeader,
                                modifier           = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                HorizontalDivider(color = CColorBorde, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun CCeldaHeader(texto: String, ancho: Dp, align: TextAlign) {
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
private fun CCeldaDato(texto: String, ancho: Dp, align: TextAlign) {
    Text(
        text      = texto,
        color     = CColorTexto,
        fontSize  = 13.sp,
        textAlign = align,
        maxLines  = 1,
        overflow  = TextOverflow.Ellipsis,
        modifier  = Modifier.width(ancho).padding(end = 8.dp)
    )
}
