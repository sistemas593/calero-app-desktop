package com.calero.lili.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.calero.lili.desktop.ui.actualizacion.ActualizacionViewModel
import com.calero.lili.desktop.ui.actualizacion.UpdateCheckState
import com.calero.lili.core.comprobantesWs.services.ProcesarDocumentosServiceImpl
import com.calero.lili.core.modAdminEmpresas.AdEmpresasServiceImpl
import com.calero.lili.core.modVentas.facturas.VtVentasFacturasServiceImpl
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesServiceImpl
import com.calero.lili.core.modComprasItems.GeItemsServiceImpl
import com.calero.lili.core.modComprasItemsMedidas.GeItemsMedidasServiceImpl
import com.calero.lili.core.modTerceros.GeTercerosServiceImpl
import com.calero.lili.desktop.ui.components.MenuOpcion
import com.calero.lili.desktop.ui.components.Sidebar
import com.calero.lili.desktop.ui.empresas.EmpresaFormScreen
import com.calero.lili.desktop.ui.empresas.EmpresaFormViewModel
import com.calero.lili.desktop.ui.empresas.EmpresasScreen
import com.calero.lili.desktop.ui.empresas.EmpresasViewModel
import com.calero.lili.desktop.ui.empresas.series.SerieFormScreen
import com.calero.lili.desktop.ui.empresas.series.SerieFormViewModel
import com.calero.lili.desktop.ui.empresas.series.SeriesScreen
import com.calero.lili.desktop.ui.empresas.series.SeriesViewModel
import com.calero.lili.desktop.ui.items.ItemFormScreen
import com.calero.lili.desktop.ui.items.ItemFormViewModel
import com.calero.lili.desktop.ui.items.ItemsScreen
import com.calero.lili.desktop.ui.items.ItemsViewModel
import com.calero.lili.desktop.ui.items.medidas.MedidaFormScreen
import com.calero.lili.desktop.ui.items.medidas.MedidaFormViewModel
import com.calero.lili.desktop.ui.items.medidas.MedidasScreen
import com.calero.lili.desktop.ui.items.medidas.MedidasViewModel
import com.calero.lili.desktop.ui.selector.SelectorEmpresaScreen
import com.calero.lili.desktop.ui.ventas.facturas.FacturasScreen
import com.calero.lili.desktop.ui.ventas.facturas.FacturasViewModel
import com.calero.lili.desktop.ui.selector.SelectorEmpresaViewModel
import com.calero.lili.desktop.ui.terceros.ClienteFormScreen
import com.calero.lili.desktop.ui.terceros.ClienteFormViewModel
import com.calero.lili.desktop.ui.terceros.ClientesScreen
import com.calero.lili.desktop.ui.terceros.ClientesViewModel
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.calero.lili.core", "com.calero.lili.desktop"])
@EnableJpaRepositories(basePackages = ["com.calero.lili.core"])
@EntityScan(basePackages = ["com.calero.lili.core"])
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
open class DesktopApplication

private const val ID_DATA = 1L

/** Datos de la empresa activa en memoria. */
data class EmpresaActiva(val idEmpresa: Long, val razonSocial: String)

fun main() {
    val app = SpringApplication(DesktopApplication::class.java)
    app.webApplicationType = WebApplicationType.NONE
    app.setHeadless(false)
    val context = app.run()

    // ── Servicios Spring
    val empresasService   = context.getBean(AdEmpresasServiceImpl::class.java)
    val facturasService   = context.getBean(VtVentasFacturasServiceImpl::class.java)
    val procesarService   = context.getBean(ProcesarDocumentosServiceImpl::class.java)
    val seriesService     = context.getBean(AdEmpresasSeriesServiceImpl::class.java)
    val tercerosService   = context.getBean(GeTercerosServiceImpl::class.java)
    val medidasService    = context.getBean(GeItemsMedidasServiceImpl::class.java)
    val itemsService      = context.getBean(GeItemsServiceImpl::class.java)

    // ViewModel del selector (independiente de empresa)
    val selectorViewModel      = SelectorEmpresaViewModel(empresasService, ID_DATA)
    val actualizacionViewModel = ActualizacionViewModel()

    application {
        Window(
            onCloseRequest = {
                selectorViewModel.onDestroy()
                actualizacionViewModel.onDestroy()
                exitApplication()
            },
            title = "Calero - Sistema de Gestión",
            state = WindowState(placement = WindowPlacement.Maximized)
        ) {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary      = Color(0xFF1565C0),
                    onPrimary    = Color.White,
                    surface      = Color.White,
                    background   = Color(0xFFF0F4FF),
                    error        = Color(0xFFB00020),
                    onSurface    = Color(0xFF1A1A2E),
                    onBackground = Color(0xFF1A1A2E)
                )
            ) {
                // null = pantalla de selección | EmpresaActiva = app principal
                // Siempre arranca en el selector — el guardado sólo sirve para tener el id disponible en sesión
                var empresaActiva      by remember { mutableStateOf<EmpresaActiva?>(null) }
                val updateState        by actualizacionViewModel.state.collectAsState()
                var mostrarDialogo     by remember { mutableStateOf(false) }
                var mostrarError       by remember { mutableStateOf(false) }
                var mensajeError       by remember { mutableStateOf("") }
                var linkDescarga       by remember { mutableStateOf("") }

                LaunchedEffect(updateState) {
                    when (updateState) {
                        is UpdateCheckState.UpdateAvailable -> {
                            linkDescarga   = (updateState as UpdateCheckState.UpdateAvailable).link
                            mostrarDialogo = true
                        }
                        // Los JARs fueron reemplazados por el .bat — cerrar la app para que el bat la relance
                        UpdateCheckState.ListoParaReiniciar -> exitApplication()
                        is UpdateCheckState.ErrorActualizacion -> {
                            mensajeError = (updateState as UpdateCheckState.ErrorActualizacion).mensaje
                            mostrarError = true
                        }
                        else -> {}
                    }
                }

                // ── Diálogo: nueva actualización disponible
                if (mostrarDialogo) {
                    AlertDialog(
                        onDismissRequest = {},
                        title   = { Text("Actualización disponible", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                        text    = { Text("Hay una nueva versión disponible del sistema. ¿Desea descargarla ahora?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    mostrarDialogo = false
                                    actualizacionViewModel.descargar(linkDescarga)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                            ) { Text("Sí") }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = {
                                mostrarDialogo = false
                                actualizacionViewModel.continuar()
                            }) { Text("Quizás, más tarde") }
                        }
                    )
                }

                // ── Diálogo: error durante la descarga / actualización
                if (mostrarError) {
                    AlertDialog(
                        onDismissRequest = {},
                        title   = { Text("Error de actualización", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                        text    = { Text(mensajeError) },
                        confirmButton = {
                            Button(
                                onClick = {
                                    mostrarError = false
                                    actualizacionViewModel.continuar()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                            ) { Text("Continuar sin actualizar") }
                        }
                    )
                }

                when {
                    // ── Verificando conexión con el servidor de versiones
                    updateState is UpdateCheckState.Checking -> {
                        Box(
                            modifier         = androidx.compose.ui.Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF1565C0))
                                Spacer(androidx.compose.ui.Modifier.height(12.dp))
                                Text("Verificando actualizaciones…", fontSize = 14.sp, color = Color(0xFF6B7A99))
                            }
                        }
                    }
                    // ── Descargando el ZIP de actualización
                    updateState is UpdateCheckState.Descargando -> {
                        Box(
                            modifier         = androidx.compose.ui.Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF1565C0))
                                Spacer(androidx.compose.ui.Modifier.height(12.dp))
                                Text("Descargando actualización, por favor espere…", fontSize = 14.sp, color = Color(0xFF6B7A99))
                                Spacer(androidx.compose.ui.Modifier.height(6.dp))
                                Text("No cierre la aplicación.", fontSize = 12.sp, color = Color(0xFF6B7A99))
                            }
                        }
                    }
                    // ── Actualización lista — el .bat fue lanzado, esperando cierre de la app
                    updateState is UpdateCheckState.ListoParaReiniciar -> {
                        Box(
                            modifier         = androidx.compose.ui.Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF1565C0))
                                Spacer(androidx.compose.ui.Modifier.height(12.dp))
                                Text("Actualización lista. Reiniciando aplicación…", fontSize = 14.sp, color = Color(0xFF6B7A99))
                            }
                        }
                    }
                    empresaActiva == null -> {
                        // ── Pantalla de selección de empresa
                        SelectorEmpresaScreen(
                            viewModel             = selectorViewModel,
                            onEmpresaSeleccionada = { idEmpresa, razonSocial ->
                                AppPreferences.guardarEmpresa(idEmpresa, razonSocial)
                                empresaActiva = EmpresaActiva(idEmpresa, razonSocial)
                            }
                        )
                    }
                    else -> {
                    // ── Aplicación principal
                    val idEmpresa   = empresaActiva!!.idEmpresa
                    val nombreEmpresa = empresaActiva!!.razonSocial

                    val viewModel          = remember(idEmpresa) { EmpresasViewModel(empresasService) }
                    val seriesViewModel    = remember(idEmpresa) { SeriesViewModel(seriesService) }
                    val clientesViewModel  = remember(idEmpresa) { ClientesViewModel(tercerosService) }
                    val medidasViewModel   = remember(idEmpresa) { MedidasViewModel(medidasService) }
                    val itemsViewModel     = remember(idEmpresa) { ItemsViewModel(itemsService, idData = ID_DATA, idEmpresa = idEmpresa) }
                    val facturasViewModel  = remember(idEmpresa) { FacturasViewModel(facturasService, procesarService, tercerosService, idData = ID_DATA, idEmpresa = idEmpresa) }

                    DisposableEffect(idEmpresa) {
                        onDispose {
                            viewModel.onDestroy()
                            seriesViewModel.onDestroy()
                            clientesViewModel.onDestroy()
                            medidasViewModel.onDestroy()
                            itemsViewModel.onDestroy()
                            facturasViewModel.onDestroy()
                        }
                    }

                    val empresasState  by viewModel.state.collectAsState()
                    val seriesState    by seriesViewModel.state.collectAsState()
                    val clientesState  by clientesViewModel.state.collectAsState()
                    val medidasState   by medidasViewModel.state.collectAsState()
                    val itemsState     by itemsViewModel.state.collectAsState()
                    val facturasState  by facturasViewModel.state.collectAsState()

                    var opcionActual by remember(idEmpresa) { mutableStateOf(MenuOpcion.LISTA_EMPRESAS) }

                    Row {
                        // ── Sidebar con empresa activa
                        Sidebar(
                            opcionActual     = opcionActual,
                            empresaNombre    = nombreEmpresa,
                            onSeleccionar    = { opcion ->
                                opcionActual = opcion
                                when (opcion) {
                                    MenuOpcion.LISTA_EMPRESAS -> viewModel.cerrarFormulario()
                                    MenuOpcion.LISTA_SERIES   -> seriesViewModel.cerrarFormulario()
                                    MenuOpcion.LISTA_CLIENTES -> clientesViewModel.cerrarFormulario()
                                    MenuOpcion.LISTA_MEDIDAS  -> medidasViewModel.cerrarFormulario()
                                    MenuOpcion.LISTA_ITEMS     -> itemsViewModel.cerrarFormulario()
                                    MenuOpcion.LISTA_FACTURAS  -> { /* solo lista */ }
                                }
                            },
                            onCambiarEmpresa = {
                                AppPreferences.limpiarEmpresa()
                                empresaActiva = null
                            }
                        )

                        // ── Contenido principal
                        when (opcionActual) {

                            MenuOpcion.LISTA_EMPRESAS -> {
                                if (empresasState.showForm) {
                                    val formViewModel = remember(empresasState.editingId) {
                                        EmpresaFormViewModel(service = empresasService, idEmpresa = empresasState.editingId)
                                    }
                                    DisposableEffect(empresasState.editingId) {
                                        onDispose { formViewModel.onDestroy() }
                                    }
                                    EmpresaFormScreen(
                                        viewModel = formViewModel,
                                        onCerrar  = viewModel::cerrarFormulario
                                    )
                                } else {
                                    EmpresasScreen(
                                        viewModel       = viewModel,
                                        onNuevaEmpresa  = { viewModel.abrirFormulario() },
                                        onEditarEmpresa = { id -> viewModel.abrirFormulario(id) }
                                    )
                                }
                            }

                            MenuOpcion.LISTA_SERIES -> {
                                if (seriesState.showForm) {
                                    val formViewModel = remember(seriesState.editingId) {
                                        SerieFormViewModel(service = seriesService, idSerie = seriesState.editingId)
                                    }
                                    DisposableEffect(seriesState.editingId) {
                                        onDispose { formViewModel.onDestroy() }
                                    }
                                    SerieFormScreen(
                                        viewModel = formViewModel,
                                        onCerrar  = seriesViewModel::cerrarFormulario
                                    )
                                } else {
                                    SeriesScreen(
                                        viewModel     = seriesViewModel,
                                        onNuevaSerie  = { seriesViewModel.abrirFormulario() },
                                        onEditarSerie = { id -> seriesViewModel.abrirFormulario(id) }
                                    )
                                }
                            }

                            MenuOpcion.LISTA_CLIENTES -> {
                                if (clientesState.showForm) {
                                    val formViewModel = remember(clientesState.editingId) {
                                        ClienteFormViewModel(
                                            service   = tercerosService,
                                            idCliente = clientesState.editingId
                                        )
                                    }
                                    DisposableEffect(clientesState.editingId) {
                                        onDispose { formViewModel.onDestroy() }
                                    }
                                    ClienteFormScreen(
                                        viewModel = formViewModel,
                                        onCerrar  = clientesViewModel::cerrarFormulario
                                    )
                                } else {
                                    ClientesScreen(
                                        viewModel       = clientesViewModel,
                                        onNuevoCliente  = { clientesViewModel.abrirFormulario() },
                                        onEditarCliente = { id -> clientesViewModel.abrirFormulario(id) }
                                    )
                                }
                            }

                            MenuOpcion.LISTA_MEDIDAS -> {
                                if (medidasState.showForm) {
                                    val formViewModel = remember(medidasState.editingId) {
                                        MedidaFormViewModel(
                                            service  = medidasService,
                                            idMedida = medidasState.editingId
                                        )
                                    }
                                    DisposableEffect(medidasState.editingId) {
                                        onDispose { formViewModel.onDestroy() }
                                    }
                                    MedidaFormScreen(
                                        viewModel = formViewModel,
                                        onCerrar  = medidasViewModel::cerrarFormulario
                                    )
                                } else {
                                    MedidasScreen(
                                        viewModel      = medidasViewModel,
                                        onNuevaMedida  = { medidasViewModel.abrirFormulario() },
                                        onEditarMedida = { id -> medidasViewModel.abrirFormulario(id) }
                                    )
                                }
                            }

                            MenuOpcion.LISTA_ITEMS -> {
                                if (itemsState.showForm) {
                                    val formViewModel = remember(itemsState.editingId) {
                                        ItemFormViewModel(
                                            service        = itemsService,
                                            medidasService = medidasService,
                                            idItem         = itemsState.editingId,
                                            idData         = ID_DATA,
                                            idEmpresa      = idEmpresa
                                        )
                                    }
                                    DisposableEffect(itemsState.editingId) {
                                        onDispose { formViewModel.onDestroy() }
                                    }
                                    ItemFormScreen(
                                        viewModel = formViewModel,
                                        onCerrar  = itemsViewModel::cerrarFormulario
                                    )
                                } else {
                                    ItemsScreen(
                                        viewModel    = itemsViewModel,
                                        onNuevoItem  = { itemsViewModel.abrirFormulario() },
                                        onEditarItem = { id -> itemsViewModel.abrirFormulario(id) }
                                    )
                                }
                            }

                            MenuOpcion.LISTA_FACTURAS -> {
                                FacturasScreen(viewModel = facturasViewModel)
                            }
                        }
                    }
                }
                } // when
            }
        }
    }
}
