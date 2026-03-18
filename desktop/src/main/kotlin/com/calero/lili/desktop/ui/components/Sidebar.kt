package com.calero.lili.desktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SidebarBg       = Color(0xFF0D1B2A)
private val SidebarItemHover = Color(0xFF1B2838)
private val SidebarItemActive = Color(0xFF1565C0)
private val SidebarTextColor = Color(0xFFB0BEC5)
private val SidebarTextActive = Color.White
private val SidebarDivider   = Color(0xFF1B2838)

enum class MenuOpcion(val titulo: String) {
    LISTA_EMPRESAS("Lista de Empresas"),
    LISTA_SERIES("Series"),
    LISTA_CLIENTES("Clientes")
}

@Composable
fun Sidebar(
    opcionActual: MenuOpcion,
    onSeleccionar: (MenuOpcion) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(230.dp)
            .background(SidebarBg)
            .padding(vertical = 16.dp)
    ) {
        // ── Logo / Título de la app
        Text(
            text = "CALERO",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        Text(
            text = "Sistema de Gestión",
            color = SidebarTextColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 20.dp, bottom = 16.dp)
        )

        HorizontalDivider(color = SidebarDivider, thickness = 1.dp)

        Spacer(Modifier.height(12.dp))

        // ── Sección: Empresas
        SeccionLabel("EMPRESAS")
        SidebarItem(
            texto      = MenuOpcion.LISTA_EMPRESAS.titulo,
            isSelected = opcionActual == MenuOpcion.LISTA_EMPRESAS,
            onClick    = { onSeleccionar(MenuOpcion.LISTA_EMPRESAS) }
        )
        SidebarItem(
            texto      = MenuOpcion.LISTA_SERIES.titulo,
            isSelected = opcionActual == MenuOpcion.LISTA_SERIES,
            onClick    = { onSeleccionar(MenuOpcion.LISTA_SERIES) }
        )

        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = SidebarDivider, thickness = 1.dp)
        Spacer(Modifier.height(8.dp))

        // ── Sección: Terceros
        SeccionLabel("TERCEROS")
        SidebarItem(
            texto      = MenuOpcion.LISTA_CLIENTES.titulo,
            isSelected = opcionActual == MenuOpcion.LISTA_CLIENTES,
            onClick    = { onSeleccionar(MenuOpcion.LISTA_CLIENTES) }
        )

        Spacer(Modifier.weight(1f))

        // ── Pie del sidebar
        HorizontalDivider(color = SidebarDivider, thickness = 1.dp)
        Text(
            text = "v1.0.0",
            color = SidebarTextColor.copy(alpha = 0.5f),
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun SeccionLabel(texto: String) {
    Text(
        text          = texto,
        color         = SidebarTextColor,
        fontSize      = 11.sp,
        fontWeight    = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        modifier      = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
private fun SidebarItem(
    texto: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor   = if (isSelected) SidebarItemActive else Color.Transparent
    val textColor = if (isSelected) SidebarTextActive else SidebarTextColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = texto,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
