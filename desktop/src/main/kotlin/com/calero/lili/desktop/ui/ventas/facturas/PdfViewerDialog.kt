package com.calero.lili.desktop.ui.ventas.facturas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.Loader
import org.apache.pdfbox.printing.PDFPageable
import org.apache.pdfbox.rendering.PDFRenderer
import java.awt.print.PrinterJob

private suspend fun renderPdfPages(bytes: ByteArray): List<ImageBitmap> =
    withContext(Dispatchers.Default) {
        Loader.loadPDF(bytes).use { doc ->
            val renderer = PDFRenderer(doc)
            (0 until doc.numberOfPages).map { i ->
                renderer.renderImageWithDPI(i, 150f).toComposeImageBitmap()
            }
        }
    }

@Composable
fun PdfViewerDialog(
    pdfBytes   : ByteArray?,
    isLoading  : Boolean,
    nombre     : String,
    onImprimir : () -> Unit,
    onDescargar: () -> Unit,
    onCerrar   : () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier       = Modifier.fillMaxSize(0.92f),
            shape          = MaterialTheme.shapes.large,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Barra superior
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "Vista previa — ${nombre.ifBlank { "Factura" }}",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color(0xFF1565C0)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick  = onImprimir,
                            enabled  = pdfBytes != null && !isLoading
                        ) { Text("Imprimir") }
                        OutlinedButton(
                            onClick  = onDescargar,
                            enabled  = pdfBytes != null && !isLoading
                        ) { Text("Descargar") }
                        Button(
                            onClick = onCerrar,
                            colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                        ) { Text("Cerrar") }
                    }
                }

                HorizontalDivider()

                // ── Contenido
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    when {
                        isLoading -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF1565C0))
                            Spacer(Modifier.height(8.dp))
                            Text("Generando PDF…", fontSize = 13.sp, color = Color(0xFF6B7A99))
                        }

                        pdfBytes != null -> {
                            val pages by produceState<List<ImageBitmap>>(emptyList(), pdfBytes) {
                                value = renderPdfPages(pdfBytes)
                            }
                            if (pages.isEmpty()) {
                                CircularProgressIndicator(color = Color(0xFF1565C0))
                            } else {
                                LazyColumn(
                                    modifier            = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding      = PaddingValues(vertical = 16.dp)
                                ) {
                                    items(pages) { page ->
                                        Image(
                                            bitmap             = page,
                                            contentDescription = "Página PDF",
                                            modifier           = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }

                        else -> Text("PDF no disponible", fontSize = 14.sp, color = Color(0xFF6B7A99))
                    }
                }
            }
        }
    }
}
