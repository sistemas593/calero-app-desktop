package com.calero.lili.desktop.ui.empresas

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calero.lili.core.enums.TipoContribuyente
import kotlinx.coroutines.delay

private val FColorHeader = Color(0xFF1565C0)
private val FColorBorde  = Color(0xFFDDE3F0)
private val FColorTexto  = Color(0xFF1A1A2E)
private val FColorBg     = Color(0xFFF0F4FF)
private val FColorExito  = Color(0xFF2E7D32)

private val tabTitles = listOf(
    "Datos Generales",
    "Información Fiscal",
    "Contador y Representante",
    "Firma y Envío"
)

@Composable
fun EmpresaFormScreen(
    viewModel: EmpresaFormViewModel,
    onCerrar: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

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
        // ── Barra de título (fija)
        TituloFormBar(isEditMode = state.isEditMode, onCerrar = onCerrar)

        // ── Mensajes de feedback (fijos, siempre visibles)
        state.errorMessage?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
        state.successMessage?.let { msg ->
            Text(
                text = msg,
                color = FColorExito,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }

        // ── Pestañas
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = FColorHeader,
            divider = { HorizontalDivider(color = FColorBorde) }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick  = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    selectedContentColor   = FColorHeader,
                    unselectedContentColor = FColorTexto.copy(alpha = 0.6f)
                )
            }
        }

        // ── Contenido de la pestaña seleccionada (con scroll)
        if (state.isLoading) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
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
                when (selectedTab) {
                    0 -> TabDatosGenerales(state, viewModel)
                    1 -> TabFiscal(state, viewModel)
                    2 -> TabContadorRepresentante(state, viewModel)
                    3 -> TabFirmaEnvio(state, viewModel)
                }
            }
        }

        // ── Barra de acciones (fija)
        AccionBar(
            isSaving   = state.isSaving,
            onGuardar  = viewModel::guardar,
            onCancelar = onCerrar
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Barra de título
// ════════════════════════════════════════════════════════════════════════════
@Composable
private fun TituloFormBar(isEditMode: Boolean, onCerrar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FColorHeader)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isEditMode) "Editar Empresa" else "Nueva Empresa",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onCerrar) {
            Text("✕  Cerrar", color = Color.White, fontSize = 14.sp)
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Pestaña 1 — Datos Generales
// ════════════════════════════════════════════════════════════════════════════
@Composable
private fun TabDatosGenerales(state: EmpresaFormUiState, vm: EmpresaFormViewModel) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(state.razonSocial, vm::setRazonSocial, "Razón Social *", Modifier.weight(1.6f))
        FormTextField(state.ruc,         vm::setRuc,         "RUC *",          Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(state.email,  vm::setEmail,  "Email",  Modifier.weight(1f))
        FormTextField(state.ciudad, vm::setCiudad, "Ciudad", Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(state.telefono1, vm::setTelefono1, "Teléfono 1", Modifier.weight(1f))
        FormTextField(state.telefono2, vm::setTelefono2, "Teléfono 2", Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(state.direccionMatriz, vm::setDireccionMatriz, "Dirección Matriz *", Modifier.weight(1.6f))
        FormTextField(state.numero,          vm::setNumero,          "Número",             Modifier.weight(1f))
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Pestaña 2 — Información Fiscal
// ════════════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabFiscal(state: EmpresaFormUiState, vm: EmpresaFormViewModel) {
    // Dropdown tipo contribuyente (full-width)
    var expandedTipo by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expandedTipo,
        onExpandedChange = { expandedTipo = it }
    ) {
        OutlinedTextField(
            value = state.tipoContribuyente?.tipoContribuyente ?: "— Seleccione tipo de contribuyente —",
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo Contribuyente *", fontSize = 13.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = FColorHeader,
                unfocusedBorderColor = FColorBorde,
                focusedLabelColor    = FColorHeader
            )
        )
        ExposedDropdownMenu(
            expanded = expandedTipo,
            onDismissRequest = { expandedTipo = false }
        ) {
            TipoContribuyente.values().forEach { tipo ->
                DropdownMenuItem(
                    text = { Text("${tipo.name} — ${tipo.tipoContribuyente}") },
                    onClick = { vm.setTipoContribuyente(tipo); expandedTipo = false }
                )
            }
        }
    }

    Spacer(Modifier.height(4.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SiNoDropdown(state.obligadoContabilidad, vm::setObligadoContabilidad, "Obligado Contabilidad *", Modifier.weight(1f))
        SiNoDropdown(state.devolucionIva,         vm::setDevolucionIva,         "Devolución IVA",          Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SiNoDropdown(state.agenteRetencion,        vm::setAgenteRetencion,        "Agente Retención",         Modifier.weight(1f))
        FormTextField(state.contribuyenteEspecial, vm::setContribuyenteEspecial,  "Contribuyente Especial *", Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(state.formaPagoSri,   vm::setFormaPagoSri,   "Forma Pago SRI",  Modifier.weight(1f))
        FormTextField(state.codigoSustento, vm::setCodigoSustento, "Código Sustento", Modifier.weight(1f))
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Pestaña 3 — Contador y Representante
// ════════════════════════════════════════════════════════════════════════════
@Composable
private fun TabContadorRepresentante(state: EmpresaFormUiState, vm: EmpresaFormViewModel) {
    Text(
        text = "Datos del Contador",
        color = FColorHeader,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(state.contadorNombre, vm::setContadorNombre, "Nombre del Contador", Modifier.weight(1f))
        FormTextField(state.contadorRuc,    vm::setContadorRuc,    "RUC del Contador",    Modifier.weight(1f))
    }

    Spacer(Modifier.height(12.dp))
    HorizontalDivider(color = FColorBorde)
    Spacer(Modifier.height(12.dp))

    Text(
        text = "Datos del Representante Legal",
        color = FColorHeader,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(state.representanteNombre,               vm::setRepresentanteNombre,               "Nombre del Representante",       Modifier.weight(1.6f))
        FormTextField(state.representanteTipoIdentificacion,   vm::setRepresentanteTipoIdentificacion,   "Tipo Identif. Representante",    Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(state.representanteIdentificacion, vm::setRepresentanteIdentificacion, "N° Identificación Representante", Modifier.weight(1f))
        Spacer(Modifier.weight(1f))
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Pestaña 4 — Firma Digital y Momentos de Envío
// ════════════════════════════════════════════════════════════════════════════
@Composable
private fun TabFirmaEnvio(state: EmpresaFormUiState, vm: EmpresaFormViewModel) {
    Text(
        text = "Firma Digital",
        color = FColorHeader,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(
            value = state.contraseniaFirma,
            onValueChange = vm::setContraseniaFirma,
            label = "Contraseña de Firma",
            modifier = Modifier.weight(1f),
            isPassword = true
        )
        FormTextField(
            value = state.fechaCaducidadCertificado,
            onValueChange = vm::setFechaCaducidadCertificado,
            label = "Caducidad Certificado (dd/MM/yyyy)",
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(Modifier.height(12.dp))

    // ── Selección de archivo .p12
    Text("Archivo de Firma (.p12)", fontSize = 12.sp, color = FColorBorde.copy(alpha = 0f).let { FColorHeader }, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(4.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value         = state.rutaArchivoFirma,
            onValueChange = vm::setRutaArchivoFirma,
            placeholder   = { Text("Seleccione o escriba la ruta del .p12…", fontSize = 12.sp) },
            singleLine    = true,
            modifier      = Modifier.weight(1f),
            textStyle     = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FColorHeader, unfocusedBorderColor = FColorBorde)
        )
        Button(
            onClick = {
                val chooser = javax.swing.JFileChooser()
                chooser.dialogTitle = "Seleccione el archivo de firma digital (.p12)"
                chooser.fileSelectionMode = javax.swing.JFileChooser.FILES_ONLY
                chooser.fileFilter = javax.swing.filechooser.FileNameExtensionFilter(
                    "Certificado digital (*.p12, *.pfx)", "p12", "pfx")
                if (chooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
                    vm.setRutaArchivoFirma(chooser.selectedFile.absolutePath)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = FColorHeader)
        ) { Text("Examinar", fontSize = 13.sp) }
    }

    Spacer(Modifier.height(8.dp))

    // ── Selección de logo (opcional)
    Text("Logo de la Empresa (opcional)", fontSize = 12.sp, color = FColorHeader, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(4.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value         = state.rutaLogo,
            onValueChange = vm::setRutaLogo,
            placeholder   = { Text("Seleccione o escriba la ruta del logo…", fontSize = 12.sp) },
            singleLine    = true,
            modifier      = Modifier.weight(1f),
            textStyle     = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FColorHeader, unfocusedBorderColor = FColorBorde)
        )
        Button(
            onClick = {
                val chooser = javax.swing.JFileChooser()
                chooser.dialogTitle = "Seleccione el logo de la empresa"
                chooser.fileSelectionMode = javax.swing.JFileChooser.FILES_ONLY
                chooser.fileFilter = javax.swing.filechooser.FileNameExtensionFilter(
                    "Imagen (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg")
                if (chooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
                    vm.setRutaLogo(chooser.selectedFile.absolutePath)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = FColorHeader)
        ) { Text("Examinar", fontSize = 13.sp) }
    }

    Spacer(Modifier.height(12.dp))
    HorizontalDivider(color = FColorBorde)
    Spacer(Modifier.height(12.dp))

    Text(
        text = "Momento de Envío por Tipo de Documento",
        color = FColorHeader,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(Modifier.height(4.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MomentoEnvioDropdown(state.momentoEnvioFactura,      vm::setMomentoEnvioFactura,      "Factura",      Modifier.weight(1f))
        MomentoEnvioDropdown(state.momentoEnvioNotaCredito,  vm::setMomentoEnvioNotaCredito,  "Nota Crédito", Modifier.weight(1f))
        MomentoEnvioDropdown(state.momentoEnvioNotaDebito,   vm::setMomentoEnvioNotaDebito,   "Nota Débito",  Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MomentoEnvioDropdown(state.momentoEnvioGuiaRemision,          vm::setMomentoEnvioGuiaRemision,          "Guía Remisión",     Modifier.weight(1f))
        MomentoEnvioDropdown(state.momentoEnvioLiquidacion,           vm::setMomentoEnvioLiquidacion,           "Liquidación",       Modifier.weight(1f))
        MomentoEnvioDropdown(state.momentoEnvioComprobanteRetencion,  vm::setMomentoEnvioComprobanteRetencion,  "Comp. Retención",   Modifier.weight(1f))
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Componentes reutilizables
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = FColorHeader,
            unfocusedBorderColor = FColorBorde,
            focusedLabelColor    = FColorHeader
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SiNoDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val opciones = listOf("S" to "Sí", "N" to "No")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = opciones.find { it.first == value }?.second ?: value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 13.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = FColorHeader,
                unfocusedBorderColor = FColorBorde,
                focusedLabelColor    = FColorHeader
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { (valor, etiqueta) ->
                DropdownMenuItem(
                    text = { Text(etiqueta) },
                    onClick = { onValueChange(valor); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MomentoEnvioDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val opciones = listOf("0", "1", "2")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 13.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = FColorHeader,
                unfocusedBorderColor = FColorBorde,
                focusedLabelColor    = FColorHeader
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { v ->
                DropdownMenuItem(
                    text = { Text(v) },
                    onClick = { onValueChange(v); expanded = false }
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Barra de acciones inferior (fija)
// ════════════════════════════════════════════════════════════════════════════
@Composable
private fun AccionBar(
    isSaving: Boolean,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    HorizontalDivider(color = FColorBorde)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onCancelar,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Text("Cancelar")
        }
        Button(
            onClick = onGuardar,
            enabled = !isSaving,
            colors = ButtonDefaults.buttonColors(containerColor = FColorHeader)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text("Guardando...")
            } else {
                Text("Guardar")
            }
        }
    }
}
