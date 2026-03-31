package com.calero.lili.desktop.ui.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val FColorHeader = Color(0xFF1565C0)
private val FColorBorde  = Color(0xFFDDE3F0)
private val FColorTexto  = Color(0xFF1A1A2E)
private val FColorBg     = Color(0xFFF0F4FF)
private val FColorExito  = Color(0xFF2E7D32)

private val tabTitles = listOf("Datos del Item", "Precios", "Detalles Adicionales")

// Columnas tabla detalles adicionales
private val COL_NOMBRE_DET   = 220.dp
private val COL_VALOR_DET    = 400.dp
private val COL_ACCIONES_DET = 60.dp

@Composable
fun ItemFormScreen(
    viewModel: ItemFormViewModel,
    onCerrar: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) { delay(900); onCerrar() }
    }

    Column(modifier = Modifier.fillMaxSize().background(FColorBg)) {

        // ── Cabecera
        Row(
            modifier = Modifier.fillMaxWidth().background(FColorHeader).padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = if (state.isEditMode) "Editar Item" else "Nuevo Item",
                color      = Color.White,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onCerrar) { Text("✕  Cerrar", color = Color.White, fontSize = 14.sp) }
        }

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
                        colors  = ButtonDefaults.buttonColors(containerColor = FColorHeader)
                    ) { Text("Aceptar") }
                }
            )
        }

        state.successMessage?.let { msg ->
            Text(
                text       = msg,
                color      = FColorExito,
                fontWeight = FontWeight.Medium,
                fontSize   = 14.sp,
                modifier   = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }

        // ── Pestañas
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor   = Color.White,
            contentColor     = FColorHeader,
            divider          = { HorizontalDivider(color = FColorBorde) }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick  = { selectedTab = index },
                    text = {
                        Text(
                            text       = title,
                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize   = 13.sp
                        )
                    },
                    selectedContentColor   = FColorHeader,
                    unselectedContentColor = FColorTexto.copy(alpha = 0.6f)
                )
            }
        }

        // ── Contenido
        if (state.isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FColorHeader)
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    0 -> TabDatos(state, viewModel)
                    1 -> TabPrecios(state, viewModel)
                    2 -> TabDetalles(state, viewModel)
                }
            }
        }

        // ── Barra de acciones
        HorizontalDivider(color = FColorBorde)
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onCerrar, modifier = Modifier.padding(end = 12.dp)) { Text("Cancelar") }
            Button(
                onClick  = viewModel::guardar,
                enabled  = !state.isSaving,
                colors   = ButtonDefaults.buttonColors(containerColor = FColorHeader)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}

// ── Pestaña 0: Datos del Item ──────────────────────────────────────────────────
@Composable
private fun TabDatos(state: ItemFormUiState, viewModel: ItemFormViewModel) {
    Text("Datos del Item", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = FColorHeader)
    HorizontalDivider(color = FColorBorde, thickness = 1.dp)

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value         = state.codigoPrincipal,
            onValueChange = viewModel::setCodigoPrincipal,
            label         = { Text("Código Principal *", fontSize = 13.sp) },
            singleLine    = true,
            modifier      = Modifier.weight(1f),
            colors        = campoColors()
        )
        OutlinedTextField(
            value         = state.codigoAuxiliar,
            onValueChange = viewModel::setCodigoAuxiliar,
            label         = { Text("Código Auxiliar", fontSize = 13.sp) },
            singleLine    = true,
            modifier      = Modifier.weight(1f),
            colors        = campoColors()
        )
        OutlinedTextField(
            value         = state.codigoBarras,
            onValueChange = viewModel::setCodigoBarras,
            label         = { Text("Código de Barras", fontSize = 13.sp) },
            singleLine    = true,
            modifier      = Modifier.weight(1f),
            colors        = campoColors()
        )
    }

    OutlinedTextField(
        value         = state.descripcion,
        onValueChange = viewModel::setDescripcion,
        label         = { Text("Descripción *", fontSize = 13.sp) },
        singleLine    = true,
        modifier      = Modifier.fillMaxWidth(0.75f),
        colors        = campoColors()
    )

    OutlinedTextField(
        value         = state.caracteristicas,
        onValueChange = viewModel::setCaracteristicas,
        label         = { Text("Características", fontSize = 13.sp) },
        minLines      = 3,
        maxLines      = 5,
        modifier      = Modifier.fillMaxWidth(0.75f),
        colors        = campoColors()
    )

    Spacer(Modifier.height(4.dp))
    Text("Medidas", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = FColorHeader)
    HorizontalDivider(color = FColorBorde, thickness = 1.dp)

    MedidaDropdown(
        disponibles   = state.medidasDisponibles,
        seleccionada  = state.medidaSeleccionada,
        onSeleccionar = viewModel::setMedida
    )
}

// ── Pestaña 1: Precios ─────────────────────────────────────────────────────────
@Composable
private fun TabPrecios(state: ItemFormUiState, viewModel: ItemFormViewModel) {
    Text("Precios", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = FColorHeader)
    HorizontalDivider(color = FColorBorde, thickness = 1.dp)

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        PrecioCampo("Precio 1", state.precio1, viewModel::setPrecio1, Modifier.weight(1f))
        PrecioCampo("Precio 2", state.precio2, viewModel::setPrecio2, Modifier.weight(1f))
        PrecioCampo("Precio 3", state.precio3, viewModel::setPrecio3, Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        PrecioCampo("Precio 4", state.precio4, viewModel::setPrecio4, Modifier.weight(1f))
        PrecioCampo("Precio 5", state.precio5, viewModel::setPrecio5, Modifier.weight(1f))
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun PrecioCampo(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier) {
    OutlinedTextField(
        value         = value,
        onValueChange = onChange,
        label         = { Text(label, fontSize = 13.sp) },
        singleLine    = true,
        modifier      = modifier,
        colors        = campoColors()
    )
}

// ── Pestaña 2: Detalles Adicionales ───────────────────────────────────────────
@Composable
private fun TabDetalles(state: ItemFormUiState, viewModel: ItemFormViewModel) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = viewModel::agregarDetalle,
            colors  = ButtonDefaults.buttonColors(containerColor = FColorHeader)
        ) {
            Text("+ Agregar detalle")
        }
    }

    Spacer(Modifier.height(8.dp))

    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp,
        color           = Color.White
    ) {
        Column {
            // Encabezado azul (igual que SeriesScreen)
            Row(
                modifier          = Modifier.background(FColorHeader).padding(vertical = 10.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CeldaHeaderDet("Nombre",   COL_NOMBRE_DET)
                CeldaHeaderDet("Valor",    COL_VALOR_DET)
                CeldaHeaderDet("Acciones", COL_ACCIONES_DET)
            }
            HorizontalDivider(color = FColorBorde)

            if (state.detallesAdicionales.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin detalles adicionales", color = Color(0xFF9E9E9E), fontSize = 13.sp)
                }
            } else {
                state.detallesAdicionales.forEachIndexed { idx, detalle ->
                    Row(
                        modifier          = Modifier
                            .background(if (idx % 2 == 0) Color(0xFFF5F7FF) else Color.White)
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value         = detalle.nombre,
                            onValueChange = { viewModel.setDetalleNombre(idx, it) },
                            singleLine    = true,
                            modifier      = Modifier.width(COL_NOMBRE_DET).padding(end = 8.dp),
                            colors        = campoColors()
                        )
                        OutlinedTextField(
                            value         = detalle.valor,
                            onValueChange = { viewModel.setDetalleValor(idx, it) },
                            singleLine    = true,
                            modifier      = Modifier.width(COL_VALOR_DET).padding(end = 8.dp),
                            colors        = campoColors()
                        )
                        Box(
                            modifier         = Modifier.width(COL_ACCIONES_DET).height(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick  = { viewModel.eliminarDetalle(idx) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint               = Color(0xFFB00020),
                                    modifier           = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = FColorBorde, thickness = 0.5.dp)
                }
            }
        }
    }
}

// ── Helpers ────────────────────────────────────────────────────────────────────
@Composable
private fun campoColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = FColorHeader,
    unfocusedBorderColor = FColorBorde,
    focusedLabelColor    = FColorHeader
)

@Composable
private fun CeldaHeaderDet(texto: String, ancho: Dp) {
    Text(
        text       = texto,
        color      = Color.White,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 13.sp,
        maxLines   = 1,
        overflow   = TextOverflow.Ellipsis,
        modifier   = Modifier.width(ancho).padding(end = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedidaDropdown(
    disponibles:   List<com.calero.lili.core.modComprasItemsMedidas.dto.GeItemMedidaReportDto>,
    seleccionada:  com.calero.lili.core.modComprasItemsMedidas.dto.GeItemMedidaReportDto?,
    onSeleccionar: (com.calero.lili.core.modComprasItemsMedidas.dto.GeItemMedidaReportDto?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier         = Modifier.fillMaxWidth(0.4f)
    ) {
        OutlinedTextField(
            value         = seleccionada?.unidadMedida ?: "",
            onValueChange = {},
            readOnly      = true,
            label         = { Text("Medida", fontSize = 13.sp) },
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier      = Modifier.menuAnchor().fillMaxWidth(),
            colors        = campoColors()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            disponibles.forEach { m ->
                DropdownMenuItem(
                    text    = { Text(m.unidadMedida ?: "", fontSize = 13.sp) },
                    onClick = { onSeleccionar(m); expanded = false }
                )
            }
        }
    }
}
