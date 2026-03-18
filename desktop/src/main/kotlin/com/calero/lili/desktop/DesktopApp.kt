package com.calero.lili.desktop

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.calero.lili.core.modAdminEmpresas.AdEmpresasServiceImpl
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesServiceImpl
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

fun main() {
    // Iniciar contexto de Spring sin servidor web (solo capa de servicio/JPA)
    val app = SpringApplication(DesktopApplication::class.java)
    app.webApplicationType = WebApplicationType.NONE
    app.setHeadless(false)
    val context = app.run()

    val service          = context.getBean(AdEmpresasServiceImpl::class.java)
    val seriesService    = context.getBean(AdEmpresasSeriesServiceImpl::class.java)
    val tercerosService  = context.getBean(GeTercerosServiceImpl::class.java)
    val viewModel        = EmpresasViewModel(service)
    val seriesViewModel  = SeriesViewModel(seriesService)
    val clientesViewModel = ClientesViewModel(tercerosService)

    application {
        Window(
            onCloseRequest = {
                viewModel.onDestroy()
                seriesViewModel.onDestroy()
                clientesViewModel.onDestroy()
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
                var opcionActual by remember { mutableStateOf(MenuOpcion.LISTA_EMPRESAS) }
                val empresasState  by viewModel.state.collectAsState()
                val seriesState    by seriesViewModel.state.collectAsState()
                val clientesState  by clientesViewModel.state.collectAsState()

                Row {
                    // ── Sidebar (fijo a la izquierda)
                    Sidebar(
                        opcionActual  = opcionActual,
                        onSeleccionar = { opcion ->
                            opcionActual = opcion
                            when (opcion) {
                                MenuOpcion.LISTA_EMPRESAS -> viewModel.cerrarFormulario()
                                MenuOpcion.LISTA_SERIES   -> seriesViewModel.cerrarFormulario()
                                MenuOpcion.LISTA_CLIENTES -> clientesViewModel.cerrarFormulario()
                            }
                        }
                    )

                    // ── Contenido principal (derecha)
                    when (opcionActual) {
                        MenuOpcion.LISTA_EMPRESAS -> {
                            if (empresasState.showForm) {
                                val formViewModel = remember(empresasState.editingId) {
                                    EmpresaFormViewModel(service = service, idEmpresa = empresasState.editingId)
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
                                    onEditarEmpresa = { idEmpresa -> viewModel.abrirFormulario(idEmpresa) }
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
                                    onEditarSerie = { idSerie -> seriesViewModel.abrirFormulario(idSerie) }
                                )
                            }
                        }

                        MenuOpcion.LISTA_CLIENTES -> {
                            if (clientesState.showForm) {
                                val formViewModel = remember(clientesState.editingId) {
                                    ClienteFormViewModel(
                                        service    = tercerosService,
                                        idCliente  = clientesState.editingId
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
                    }
                }
            }
        }
    }
}
