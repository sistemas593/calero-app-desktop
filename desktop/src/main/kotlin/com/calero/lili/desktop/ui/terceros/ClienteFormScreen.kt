package com.calero.lili.desktop.ui.terceros

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
import com.calero.lili.core.enums.TipoIdentificacion
import kotlinx.coroutines.delay

private val CFColorHeader  = Color(0xFF1565C0)
private val CFColorBorde   = Color(0xFFDDE3F0)
private val CFColorBg      = Color(0xFFF0F4FF)
private val CFColorExito   = Color(0xFF2E7D32)

@Composable
fun ClienteFormScreen(
    viewModel: ClienteFormViewModel,
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
                    colors  = ButtonDefaults.buttonColors(containerColor = CFColorHeader)
                ) { Text("Aceptar") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CFColorBg)
    ) {
        // ── Barra de título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CFColorHeader)
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = if (state.isEditMode) "Editar Cliente" else "Nuevo Cliente",
                color      = Color.White,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onCerrar) {
                Text("✕  Cerrar", color = Color.White, fontSize = 14.sp)
            }
        }

        // ── Mensaje de éxito
        state.successMessage?.let { msg ->
            Text(
                text       = msg,
                color      = CFColorExito,
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
                CircularProgressIndicator(color = CFColorHeader)
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ══════════════════════════════════════════
                // Sección: Datos Generales
                // ══════════════════════════════════════════
                CSeccionTitulo("Datos Generales")

                CFormTextField(
                    value         = state.tercero,
                    onValueChange = viewModel::setTercero,
                    label         = "Nombre / Razón Social *",
                    modifier      = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Dropdown TipoIdentificacion
                    var expandedTipoId by remember { mutableStateOf(false) }
                    @OptIn(ExperimentalMaterial3Api::class)
                    ExposedDropdownMenuBox(
                        expanded         = expandedTipoId,
                        onExpandedChange = { expandedTipoId = it },
                        modifier         = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value         = state.tipoIdentificacion?.identificacion ?: "— Seleccione —",
                            onValueChange = {},
                            readOnly      = true,
                            label         = { Text("Tipo Identificación", fontSize = 13.sp) },
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipoId) },
                            modifier      = Modifier.menuAnchor().fillMaxWidth(),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = CFColorHeader,
                                unfocusedBorderColor = CFColorBorde,
                                focusedLabelColor    = CFColorHeader
                            )
                        )
                        ExposedDropdownMenu(
                            expanded         = expandedTipoId,
                            onDismissRequest = { expandedTipoId = false }
                        ) {
                            TipoIdentificacion.values().forEach { tipo ->
                                DropdownMenuItem(
                                    text    = { Text("${tipo.name} — ${tipo.identificacion}") },
                                    onClick = {
                                        viewModel.setTipoIdentificacion(tipo)
                                        expandedTipoId = false
                                    }
                                )
                            }
                        }
                    }

                    CFormTextField(
                        value         = state.numeroIdentificacion,
                        onValueChange = viewModel::setNumeroIdentificacion,
                        label         = "Número Identificación",
                        modifier      = Modifier.weight(1.5f)
                    )
                }

                // ══════════════════════════════════════════
                // Sección: Contacto
                // ══════════════════════════════════════════
                CSeccionTitulo("Información de Contacto")

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CFormTextField(state.ciudad,    viewModel::setCiudad,    "Ciudad",    Modifier.weight(1f))
                    CFormTextField(state.direccion, viewModel::setDireccion, "Dirección", Modifier.weight(2f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CFormTextField(state.telefonos, viewModel::setTelefonos, "Teléfonos",    Modifier.weight(1f))
                    CFormTextField(state.contacto,  viewModel::setContacto,  "Contacto",    Modifier.weight(1f))
                    CFormTextField(state.email,     viewModel::setEmail,     "Email",        Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CFormTextField(state.web,              viewModel::setWeb,              "Sitio Web",             Modifier.weight(1f))
                    CFormTextField(state.tipoClienteProveedor, viewModel::setTipoClienteProveedor, "Tipo Cliente/Proveedor", Modifier.weight(1f))
                }
                CFormTextField(
                    value         = state.observaciones,
                    onValueChange = viewModel::setObservaciones,
                    label         = "Observaciones",
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = false,
                    minLines      = 2
                )

            }
        }

        // ── Barra de acciones inferior
        HorizontalDivider(color = CFColorBorde)
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
                colors  = ButtonDefaults.buttonColors(containerColor = CFColorHeader)
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
// Componentes reutilizables
// ════════════════════════════════════════════════════════════════

@Composable
private fun CSeccionTitulo(titulo: String) {
    Text(
        text       = titulo,
        fontSize   = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color      = CFColorHeader
    )
    HorizontalDivider(color = CFColorBorde, thickness = 1.dp)
}

@Composable
private fun CFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, fontSize = 13.sp) },
        singleLine    = singleLine,
        minLines      = minLines,
        modifier      = modifier.fillMaxWidth(),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = CFColorHeader,
            unfocusedBorderColor = CFColorBorde,
            focusedLabelColor    = CFColorHeader
        )
    )
}
