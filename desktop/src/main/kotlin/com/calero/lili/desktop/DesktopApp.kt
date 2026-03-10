package com.calero.lili.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.calero.lili.core.modAdminEmpresas.AdEmpresasServiceImpl
import com.calero.lili.desktop.ui.EmpresasScreen
import com.calero.lili.desktop.ui.EmpresasViewModel
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.calero.lili.core", "com.calero.lili.desktop"])
@EnableJpaRepositories(basePackages = ["com.calero.lili.core.repositories"])
@EntityScan(basePackages = ["com.calero.lili.core.entities"])
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
open class DesktopApplication

fun main() {
    // Iniciar contexto de Spring sin servidor web (solo capa de servicio/JPA)
    val app = SpringApplication(DesktopApplication::class.java)
    app.webApplicationType = WebApplicationType.NONE
    app.setHeadless(false)
    val context = app.run()

    val service = context.getBean(AdEmpresasServiceImpl::class.java)
    val viewModel = EmpresasViewModel(service)

    application {
        Window(
            onCloseRequest = {
                viewModel.onDestroy()
                exitApplication()
            },
            title = "Calero - Gestión de Empresas",
            state = WindowState(size = DpSize(1280.dp, 750.dp))
        ) {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary        = Color(0xFF1565C0),
                    onPrimary      = Color.White,
                    surface        = Color.White,
                    background     = Color(0xFFF0F4FF),
                    error          = Color(0xFFB00020),
                    onSurface      = Color(0xFF1A1A2E),
                    onBackground   = Color(0xFF1A1A2E)
                )
            ) {
                EmpresasScreen(viewModel = viewModel)
            }
        }
    }
}
