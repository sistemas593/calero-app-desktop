package com.calero.lili.desktop.ui.empresas

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaGetListDto

private val COL_ACCIONES   = 70.dp
private val COL_RUC        = 140.dp
private val COL_RAZON      = 260.dp
private val COL_CIUDAD     = 120.dp
private val COL_TELEFONO   = 120.dp
private val COL_EMAIL      = 200.dp
private val COL_REPRESENT  = 200.dp
private val COL_TIPO       = 160.dp

private val ColorHeader     = Color(0xFF1565C0)
private val ColorRowPar     = Color(0xFFF5F7FF)
private val ColorRowImpar   = Color.White
private val ColorBorde      = Color(0xFFDDE3F0)
private val ColorTexto      = Color(0xFF1A1A2E)
private val ColorTextoSub   = Color(0xFF555577)

@Composable
fun EmpresasScreen(
    viewModel: EmpresasViewModel,
    onNuevaEmpresa: () -> Unit,
    onEditarEmpresa: (Long) -> Unit = {},
    onVolver: (() -> Unit)? = null
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4FF))
            .padding(20.dp)
    ) {
        // ── Título
        Row(
            modifier              = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text       = "Administración de Empresas",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = ColorTexto
            )
            if (onVolver != null) {
                OutlinedButton(onClick = onVolver) {
                    Text("← Volver al selector")
                }
            }
        }

        // ── Barra de filtros
        FiltrosBar(
            filterText  = state.filterText,
            estadoFiltro = state.estadoFiltro,
            isLoading   = state.isLoading,
            onFilterChange  = viewModel::setFilter,
            onEstadoChange  = viewModel::setEstado,
            onBuscar        = viewModel::buscar
        )

        // ── Botón Nueva Empresa
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onNuevaEmpresa,
                colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
            ) {
                Text("+ Nueva Empresa")
            }
        }

        Spacer(Modifier.height(4.dp))

        // ── Mensaje de error
        state.errorMessage?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // ── Tabla
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.empresas.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron empresas.", color = ColorTextoSub)
                }
            }
            else -> {
                Surface(
                    modifier   = Modifier.weight(1f).fillMaxWidth(),
                    shape      = MaterialTheme.shapes.medium,
                    shadowElevation = 2.dp,
                    color      = Color.White
                ) {
                    Column {
                        TablaEmpresas(
                            empresas = state.empresas,
                            onEditar = onEditarEmpresa
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Paginación
        state.paginator?.let { pag ->
            PaginacionBar(
                paginaActual  = state.currentPage,
                totalPaginas  = pag.totalPages,
                totalElementos = pag.totalElements ?: 0L,
                isFirst       = pag.first ?: true,
                isLast        = pag.last ?: true,
                onAnterior    = viewModel::paginaAnterior,
                onSiguiente   = viewModel::paginaSiguiente
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltrosBar(
    filterText    : String,
    estadoFiltro  : Int?,
    isLoading     : Boolean,
    onFilterChange: (String) -> Unit,
    onEstadoChange: (Int?) -> Unit,
    onBuscar      : () -> Unit
) {
    var expandedEstado by remember { mutableStateOf(false) }

    val estadoOpciones = listOf(
        null to "Todos",
        1    to "Activo",
        0    to "Inactivo"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value         = filterText,
            onValueChange = onFilterChange,
            label         = { Text("Buscar (RUC / Razón Social)") },
            singleLine    = true,
            modifier      = Modifier.weight(1f),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Color(0xFF1565C0),
                unfocusedBorderColor = ColorBorde
            )
        )

        ExposedDropdownMenuBox(
            expanded         = expandedEstado,
            onExpandedChange = { expandedEstado = it },
            modifier         = Modifier.width(150.dp)
        ) {
            OutlinedTextField(
                value         = estadoOpciones.find { it.first == estadoFiltro }?.second ?: "Todos",
                onValueChange = {},
                readOnly      = true,
                label         = { Text("Estado") },
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstado) },
                modifier      = Modifier.menuAnchor(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Color(0xFF1565C0),
                    unfocusedBorderColor = ColorBorde
                )
            )
            ExposedDropdownMenu(
                expanded         = expandedEstado,
                onDismissRequest = { expandedEstado = false }
            ) {
                estadoOpciones.forEach { (valor, etiqueta) ->
                    DropdownMenuItem(
                        text    = { Text(etiqueta) },
                        onClick = {
                            onEstadoChange(valor)
                            expandedEstado = false
                        }
                    )
                }
            }
        }

        Button(
            onClick  = onBuscar,
            enabled  = !isLoading,
            colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text("Buscar")
            }
        }
    }
}

@Composable
private fun TablaEmpresas(
    empresas: List<AdEmpresaGetListDto>,
    onEditar: (Long) -> Unit
) {
    val scrollH = rememberScrollState()

    Column(modifier = Modifier.horizontalScroll(scrollH)) {
        EncabezadoTabla()
        HorizontalDivider(color = ColorBorde)

        LazyColumn {
            itemsIndexed(empresas) { idx, empresa ->
                FilaEmpresa(
                    empresa  = empresa,
                    bgColor  = if (idx % 2 == 0) ColorRowPar else ColorRowImpar,
                    onEditar = { onEditar(empresa.idEmpresa) }
                )
                HorizontalDivider(color = ColorBorde, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun EncabezadoTabla() {
    Row(
        modifier = Modifier
            .background(ColorHeader)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CeldaHeader("RUC",           COL_RUC,       TextAlign.Start)
        CeldaHeader("Razón Social",  COL_RAZON,     TextAlign.Start)
        CeldaHeader("Ciudad",        COL_CIUDAD,    TextAlign.Start)
        CeldaHeader("Teléfono",      COL_TELEFONO,  TextAlign.Start)
        CeldaHeader("Email",         COL_EMAIL,     TextAlign.Start)
        CeldaHeader("Representante", COL_REPRESENT, TextAlign.Start)
        CeldaHeader("Tipo Contrib.", COL_TIPO,      TextAlign.Start)
        CeldaHeader("Acciones",      COL_ACCIONES,  TextAlign.Center)
    }
}

@Composable
private fun FilaEmpresa(
    empresa : AdEmpresaGetListDto,
    bgColor : Color,
    onEditar: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(bgColor)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CeldaDato(empresa.ruc ?: "-",                  COL_RUC,       TextAlign.Start)
        CeldaDato(empresa.razonSocial ?: "-",           COL_RAZON,     TextAlign.Start)
        CeldaDato(empresa.ciudad ?: "-",               COL_CIUDAD,    TextAlign.Start)
        CeldaDato(empresa.telefono1 ?: "-",            COL_TELEFONO,  TextAlign.Start)
        CeldaDato(empresa.email ?: "-",                COL_EMAIL,     TextAlign.Start)
        CeldaDato(empresa.representanteNombre ?: "-",  COL_REPRESENT, TextAlign.Start)
        CeldaDato(empresa.tipoContribuyente?.name ?: "-", COL_TIPO,   TextAlign.Start)
        // Icono de lápiz para editar
        Box(
            modifier = Modifier.width(COL_ACCIONES).height(36.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onEditar,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(18.dp)
                )
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

@Composable
private fun PaginacionBar(
    paginaActual   : Int,
    totalPaginas   : Int,
    totalElementos : Long,
    isFirst        : Boolean,
    isLast         : Boolean,
    onAnterior     : () -> Unit,
    onSiguiente    : () -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text  = "Total: $totalElementos registro(s)",
            color = ColorTextoSub,
            fontSize = 13.sp
        )

        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick  = onAnterior,
                enabled  = !isFirst,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text("← Anterior")
            }

            Text(
                text      = "Página ${paginaActual + 1} de $totalPaginas",
                fontSize  = 13.sp,
                color     = ColorTexto,
                fontWeight = FontWeight.Medium
            )

            OutlinedButton(
                onClick  = onSiguiente,
                enabled  = !isLast,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text("Siguiente →")
            }
        }
    }
}
