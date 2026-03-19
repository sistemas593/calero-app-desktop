package com.calero.lili.desktop.ui.items.medidas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun MedidaFormScreen(
    viewModel: MedidaFormViewModel,
    onCerrar: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) { delay(900); onCerrar() }
    }

    Column(modifier = Modifier.fillMaxSize().background(FColorBg)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(FColorHeader).padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(text = if (state.isEditMode) "Editar Medida" else "Nueva Medida", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onCerrar) { Text("✕  Cerrar", color = Color.White, fontSize = 14.sp) }
        }

        state.errorMessage?.let { msg ->
            AlertDialog(
                onDismissRequest = viewModel::dismissError,
                icon             = { Text("⚠️", fontSize = 28.sp) },
                title            = { Text("Error", fontWeight = FontWeight.Bold) },
                text             = { Text(msg) },
                confirmButton    = {
                    Button(onClick = viewModel::dismissError, colors = ButtonDefaults.buttonColors(containerColor = FColorHeader)) { Text("Aceptar") }
                }
            )
        }

        state.successMessage?.let { msg ->
            Text(text = msg, color = FColorExito, fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
        }

        if (state.isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FColorHeader)
            }
        } else {
            Column(modifier = Modifier.weight(1f).padding(horizontal = 24.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Datos de la Medida", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = FColorHeader)
                HorizontalDivider(color = FColorBorde, thickness = 1.dp)
                OutlinedTextField(
                    value         = state.unidadMedida,
                    onValueChange = viewModel::setUnidadMedida,
                    label         = { Text("Unidad de Medida *", fontSize = 13.sp) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(0.5f),
                    colors        = OutlinedTextFieldDefaults.colors(focusedBorderColor = FColorHeader, unfocusedBorderColor = FColorBorde, focusedLabelColor = FColorHeader)
                )
            }
        }

        HorizontalDivider(color = FColorBorde)
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onCerrar, modifier = Modifier.padding(end = 12.dp)) { Text("Cancelar") }
            Button(onClick = viewModel::guardar, enabled = !state.isSaving, colors = ButtonDefaults.buttonColors(containerColor = FColorHeader)) {
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
