package com.calero.lili.desktop.ui.selector

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaGetListDto

private val ColorPrimario  = Color(0xFF1565C0)
private val ColorSecundario = Color(0xFF1E88E5)
private val ColorFondo     = Color(0xFFF0F4FF)
private val ColorBorde     = Color(0xFFDDE3F0)
private val ColorTexto     = Color(0xFF1A1A2E)
private val ColorSubtexto  = Color(0xFF6B7A99)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorEmpresaScreen(
    viewModel: SelectorEmpresaViewModel,
    onEmpresaSeleccionada: (idEmpresa: Long, razonSocial: String) -> Unit
) {
    val state       by viewModel.state.collectAsState()
    val empresas     = viewModel.empresasFiltradas()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(680.dp)
                .fillMaxHeight(0.85f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Encabezado con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Brush.horizontalGradient(listOf(ColorPrimario, ColorSecundario)))
                    .padding(horizontal = 32.dp, vertical = 28.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector         = Icons.Default.Business,
                        contentDescription  = null,
                        tint                = Color.White,
                        modifier            = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text       = "Calero · Sistema de Gestión",
                        color      = Color.White.copy(alpha = 0.85f),
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text       = "Seleccione una empresa",
                        color      = Color.White,
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── Cuerpo
            Surface(
                modifier       = Modifier.fillMaxSize(),
                color          = Color.White,
                shadowElevation = 4.dp,
                shape          = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // Buscador con autocomplete
                    val density = LocalDensity.current
                    var boxWidth by remember { mutableIntStateOf(0) }
                    Box(modifier = Modifier.onSizeChanged { boxWidth = it.width }) {
                        OutlinedTextField(
                            value         = state.busqueda,
                            onValueChange = viewModel::setBusqueda,
                            placeholder   = { Text("Buscar por nombre o RUC…", fontSize = 13.sp) },
                            leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null, tint = ColorSubtexto) },
                            trailingIcon  = {
                                if (state.buscando)
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = ColorPrimario)
                                else if (state.busqueda.isNotBlank())
                                    IconButton(
                                        onClick  = { viewModel.setBusqueda("") },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(Icons.Default.Clear, contentDescription = "Limpiar", modifier = Modifier.size(14.dp))
                                    }
                            },
                            modifier   = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape      = RoundedCornerShape(10.dp),
                            colors     = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = ColorPrimario,
                                unfocusedBorderColor = ColorBorde,
                                focusedLabelColor    = ColorPrimario
                            )
                        )

                        DropdownMenu(
                            expanded         = state.dropdownVisible,
                            onDismissRequest = viewModel::cerrarDropdown,
                            modifier         = Modifier
                                .width(with(density) { boxWidth.toDp() })
                                .heightIn(max = 300.dp)
                        ) {
                            state.sugerencias.forEach { empresa ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(empresa.razonSocial ?: "—", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            Text("RUC: ${empresa.ruc ?: "—"}", fontSize = 11.sp, color = ColorSubtexto)
                                        }
                                    },
                                    onClick = {
                                        val id     = empresa.idEmpresa ?: return@DropdownMenuItem
                                        val nombre = empresa.razonSocial ?: ""
                                        viewModel.cerrarDropdown()
                                        onEmpresaSeleccionada(id, nombre)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    when {
                        state.isLoading -> {
                            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = ColorPrimario, strokeWidth = 3.dp)
                            }
                        }
                        state.errorMessage != null -> {
                            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                                Text(
                                    text     = state.errorMessage ?: "",
                                    color    = Color(0xFFB00020),
                                    fontSize = 14.sp
                                )
                            }
                        }
                        empresas.isEmpty() -> {
                            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                                Text("No se encontraron empresas", color = ColorSubtexto, fontSize = 14.sp)
                            }
                        }
                        else -> {
                            // Contador
                            Text(
                                text     = "${empresas.size} empresa(s) disponible(s)",
                                color    = ColorSubtexto,
                                fontSize = 12.sp
                            )
                            Spacer(Modifier.height(8.dp))

                            LazyColumn(
                                modifier           = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(empresas) { empresa ->
                                    EmpresaCard(
                                        empresa  = empresa,
                                        onClick  = {
                                            val id     = empresa.idEmpresa ?: return@EmpresaCard
                                            val nombre = empresa.razonSocial ?: ""
                                            onEmpresaSeleccionada(id, nombre)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmpresaCard(
    empresa: AdEmpresaGetListDto,
    onClick: () -> Unit
) {
    var hovered by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (hovered) 4.dp else 1.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                hovered = false
                onClick()
            },
        color  = if (hovered) Color(0xFFEAF1FF) else Color.White,
        shape  = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = if (hovered) ColorPrimario else ColorBorde
        )
    ) {
        Row(
            modifier            = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icono empresa
            Box(
                modifier        = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ColorPrimario.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.Business,
                    contentDescription = null,
                    tint               = ColorPrimario,
                    modifier           = Modifier.size(24.dp)
                )
            }

            // Datos
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = empresa.razonSocial ?: "Sin nombre",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp,
                    color      = ColorTexto
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text     = "RUC: ${empresa.ruc ?: "—"}",
                    fontSize = 12.sp,
                    color    = ColorSubtexto
                )
                if (!empresa.ciudad.isNullOrBlank()) {
                    Text(
                        text     = empresa.ciudad,
                        fontSize = 12.sp,
                        color    = ColorSubtexto
                    )
                }
            }

            // Flecha indicadora
            Text("›", fontSize = 24.sp, color = ColorPrimario.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
        }
    }
}
