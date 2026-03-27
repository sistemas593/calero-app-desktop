package com.calero.lili.desktop.ui.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ColorPrimario   = Color(0xFF1565C0)
private val ColorSecundario = Color(0xFF1E88E5)
private val ColorFondo      = Color(0xFFF0F4FF)

@Composable
fun InicioScreen(onContinuar: () -> Unit) {
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(ColorFondo),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier            = Modifier.width(480.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Encabezado con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Brush.horizontalGradient(listOf(ColorPrimario, ColorSecundario)))
                    .padding(horizontal = 32.dp, vertical = 36.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector        = Icons.Default.Business,
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(56.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text          = "Calero · Sistema de Gestión",
                        color         = Color.White.copy(alpha = 0.85f),
                        fontSize      = 13.sp,
                        fontWeight    = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text       = "Bienvenido al sistema",
                        color      = Color.White,
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── Cuerpo blanco
            Surface(
                modifier        = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                color           = Color.White,
                shape           = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick        = onContinuar,
                        colors         = ButtonDefaults.buttonColors(containerColor = ColorPrimario),
                        shape          = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text       = "Continuar",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
