package com.calero.lili.desktop.ui.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val FColorHeader = Color(0xFF1565C0)
private val FColorBorde  = Color(0xFFDDE3F0)
private val FColorBg     = Color(0xFFF0F4FF)
private val FColorExito  = Color(0xFF2E7D32)

@Composable
fun ItemFormScreen(
    viewModel: ItemFormViewModel,
    onCerrar: () -> Unit
) {
    val state by viewModel.state.collectAsState()

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
                text     = msg,
                color    = FColorExito,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
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
                // ── Sección datos generales
                Text("Datos del Item", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = FColorHeader)
                HorizontalDivider(color = FColorBorde, thickness = 1.dp)

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value         = state.codigoPrincipal,
                        onValueChange = viewModel::setCodigoPrincipal,
                        label         = { Text("Código Principal *", fontSize = 13.sp) },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f),
                        colors        = OutlinedTextFieldDefaults.colors(focusedBorderColor = FColorHeader, unfocusedBorderColor = FColorBorde, focusedLabelColor = FColorHeader)
                    )
                    OutlinedTextField(
                        value         = state.codigoAuxiliar,
                        onValueChange = viewModel::setCodigoAuxiliar,
                        label         = { Text("Código Auxiliar", fontSize = 13.sp) },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f),
                        colors        = OutlinedTextFieldDefaults.colors(focusedBorderColor = FColorHeader, unfocusedBorderColor = FColorBorde, focusedLabelColor = FColorHeader)
                    )
                    OutlinedTextField(
                        value         = state.codigoBarras,
                        onValueChange = viewModel::setCodigoBarras,
                        label         = { Text("Código de Barras", fontSize = 13.sp) },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f),
                        colors        = OutlinedTextFieldDefaults.colors(focusedBorderColor = FColorHeader, unfocusedBorderColor = FColorBorde, focusedLabelColor = FColorHeader)
                    )
                }

                OutlinedTextField(
                    value         = state.descripcion,
                    onValueChange = viewModel::setDescripcion,
                    label         = { Text("Descripción *", fontSize = 13.sp) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(0.75f),
                    colors        = OutlinedTextFieldDefaults.colors(focusedBorderColor = FColorHeader, unfocusedBorderColor = FColorBorde, focusedLabelColor = FColorHeader)
                )

                OutlinedTextField(
                    value         = state.caracteristicas,
                    onValueChange = viewModel::setCaracteristicas,
                    label         = { Text("Características", fontSize = 13.sp) },
                    minLines      = 3,
                    maxLines      = 5,
                    modifier      = Modifier.fillMaxWidth(0.75f),
                    colors        = OutlinedTextFieldDefaults.colors(focusedBorderColor = FColorHeader, unfocusedBorderColor = FColorBorde, focusedLabelColor = FColorHeader)
                )

                // ── Sección medidas
                Spacer(Modifier.height(4.dp))
                Text("Medidas", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = FColorHeader)
                HorizontalDivider(color = FColorBorde, thickness = 1.dp)

                MedidaDropdown(
                    disponibles   = state.medidasDisponibles,
                    seleccionada  = state.medidaSeleccionada,
                    onSeleccionar = viewModel::setMedida
                )
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
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = FColorHeader,
                unfocusedBorderColor = FColorBorde,
                focusedLabelColor    = FColorHeader
            )
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
