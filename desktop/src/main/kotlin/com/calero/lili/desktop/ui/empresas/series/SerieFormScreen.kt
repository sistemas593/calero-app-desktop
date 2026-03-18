package com.calero.lili.desktop.ui.empresas.series

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calero.lili.core.enums.FormatoDocumento
import kotlinx.coroutines.delay

private val FColorHeader  = Color(0xFF1565C0)
private val FColorBorde   = Color(0xFFDDE3F0)
private val FColorBg      = Color(0xFFF0F4FF)
private val FColorExito   = Color(0xFF2E7D32)
private val FColorSection = Color(0xFFE8EEF9)

@Composable
fun SerieFormScreen(
    viewModel: SerieFormViewModel,
    onCerrar: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Auto-cierre tras guardar exitosamente
    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            delay(900)
            onCerrar()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FColorBg)
    ) {
        // ── Barra de título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FColorHeader)
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = if (state.isEditMode) "Editar Serie" else "Nueva Serie",
                color      = Color.White,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onCerrar) {
                Text("✕  Cerrar", color = Color.White, fontSize = 14.sp)
            }
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

        // ── Contenido con scroll
        if (state.isLoading) {
            Box(
                modifier         = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
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
                // ── Sección: Datos de la serie
                SeccionTitulo("Datos de la Serie")

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormTextField(state.serie,           viewModel::setSerie,           "Serie *",            Modifier.weight(1f))
                    FormTextField(state.nombreComercial, viewModel::setNombreComercial, "Nombre Comercial *", Modifier.weight(2f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormTextField(state.direccionEstablecimiento, viewModel::setDireccionEstablecimiento, "Dirección Establecimiento", Modifier.weight(2f))
                    FormTextField(state.ciudad,    viewModel::setCiudad,    "Ciudad",     Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormTextField(state.telefono1, viewModel::setTelefono1, "Teléfono 1", Modifier.weight(1f))
                    FormTextField(state.telefono2, viewModel::setTelefono2, "Teléfono 2", Modifier.weight(1f))
                }

                Spacer(Modifier.height(4.dp))

                // ── Sección: Documentos
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = "Documentos",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = FColorHeader
                    )
                    Button(
                        onClick = viewModel::agregarDocumento,
                        colors  = ButtonDefaults.buttonColors(containerColor = FColorHeader)
                    ) {
                        Text("+ Agregar Documento", fontSize = 13.sp)
                    }
                }
                HorizontalDivider(color = FColorBorde, thickness = 1.dp)

                if (state.documentos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, FColorBorde, RoundedCornerShape(8.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text     = "No hay documentos. Haga clic en \"+ Agregar Documento\" para añadir uno.",
                            color    = Color(0xFF555577),
                            fontSize = 13.sp
                        )
                    }
                } else {
                    state.documentos.forEachIndexed { idx, doc ->
                        DocumentoCard(
                            index      = idx,
                            doc        = doc,
                            onUpdate   = { updated -> viewModel.updateDocumento(idx, updated) },
                            onEliminar = { viewModel.eliminarDocumento(idx) }
                        )
                    }
                }
            }
        }

        // ── Barra de acciones inferior
        HorizontalDivider(color = FColorBorde)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick  = onCerrar,
                modifier = Modifier.padding(end = 12.dp)
            ) { Text("Cancelar") }

            Button(
                onClick = viewModel::guardar,
                enabled = !state.isSaving,
                colors  = ButtonDefaults.buttonColors(containerColor = FColorHeader)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color       = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// Card de un documento
// ════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentoCard(
    index: Int,
    doc: DocumentoFormState,
    onUpdate: (DocumentoFormState) -> Unit,
    onEliminar: () -> Unit
) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(8.dp),
        shadowElevation = 1.dp,
        color           = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Encabezado de la card
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Documento #${index + 1}",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = FColorHeader
                )
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector        = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint               = Color(0xFFB00020),
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Fila 1: tipo documento + formato
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FormTextField(
                    value         = doc.documento,
                    onValueChange = { onUpdate(doc.copy(documento = it)) },
                    label         = "Tipo Documento (ej: FAC, GRM)",
                    modifier      = Modifier.weight(1f)
                )
                // Dropdown FormatoDocumento
                var expandedFormato by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded         = expandedFormato,
                    onExpandedChange = { expandedFormato = it },
                    modifier         = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value         = doc.formatoDocumento?.let { "${it.name} — ${it.formatoDocumento}" } ?: "— Seleccione formato —",
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Formato Documento *", fontSize = 13.sp) },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFormato) },
                        modifier      = Modifier.menuAnchor().fillMaxWidth(),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = FColorHeader,
                            unfocusedBorderColor = FColorBorde,
                            focusedLabelColor    = FColorHeader
                        )
                    )
                    ExposedDropdownMenu(
                        expanded         = expandedFormato,
                        onDismissRequest = { expandedFormato = false }
                    ) {
                        FormatoDocumento.values().forEach { fmt ->
                            DropdownMenuItem(
                                text    = { Text("${fmt.name} — ${fmt.formatoDocumento}") },
                                onClick = {
                                    onUpdate(doc.copy(formatoDocumento = fmt))
                                    expandedFormato = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Fila 2: secuencial + numero autorización
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FormTextField(
                    value         = doc.numeroAutorizacion,
                    onValueChange = { onUpdate(doc.copy(numeroAutorizacion = it)) },
                    label         = "Número Autorización",
                    modifier      = Modifier.weight(1f)
                )
                FormTextField(
                    value         = doc.secuencial,
                    onValueChange = { onUpdate(doc.copy(secuencial = it)) },
                    label         = "Secuencial",
                    modifier      = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            // Fila 3: desde + hasta + fechaVencimiento
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FormTextField(
                    value         = doc.desde,
                    onValueChange = { onUpdate(doc.copy(desde = it)) },
                    label         = "Desde",
                    modifier      = Modifier.weight(1f)
                )
                FormTextField(
                    value         = doc.hasta,
                    onValueChange = { onUpdate(doc.copy(hasta = it)) },
                    label         = "Hasta",
                    modifier      = Modifier.weight(1f)
                )
                FormTextField(
                    value         = doc.fechaVencimiento,
                    onValueChange = { onUpdate(doc.copy(fechaVencimiento = it)) },
                    label         = "Fecha Vencimiento (dd/MM/yyyy)",
                    modifier      = Modifier.weight(1.4f)
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// Componentes reutilizables
// ════════════════════════════════════════════════════════════════

@Composable
private fun SeccionTitulo(titulo: String) {
    Text(
        text       = titulo,
        fontSize   = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color      = FColorHeader
    )
    HorizontalDivider(color = FColorBorde, thickness = 1.dp)
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, fontSize = 13.sp) },
        singleLine    = true,
        modifier      = modifier.fillMaxWidth(),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = FColorHeader,
            unfocusedBorderColor = FColorBorde,
            focusedLabelColor    = FColorHeader
        )
    )
}
