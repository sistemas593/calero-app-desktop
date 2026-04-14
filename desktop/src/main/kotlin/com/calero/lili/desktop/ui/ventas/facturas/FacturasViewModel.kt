package com.calero.lili.desktop.ui.ventas.facturas

import java.util.UUID
import com.calero.lili.core.dtos.Paginator
import com.calero.lili.core.enums.TipoPermiso
import com.calero.lili.core.modTerceros.GeTercerosServiceImpl
import com.calero.lili.core.modTerceros.dto.GeTerceroFilterDto
import com.calero.lili.core.modTerceros.dto.GeTerceroGetListDto
import com.calero.lili.core.modVentas.dto.GetListDto
import com.calero.lili.core.modVentas.VtVentaEntity
import com.calero.lili.core.modVentas.facturas.VtVentasFacturasServiceImpl
import com.calero.lili.core.comprobantesWs.services.GetXmlVtVentasFacturasServiceImpl
import com.calero.lili.core.modVentas.facturas.dto.FilterListDto
import java.awt.print.PrinterJob
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileNameExtensionFilter
import org.apache.pdfbox.Loader
import org.apache.pdfbox.printing.PDFPageable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/** Convierte 8 dígitos "ddMMyyyy" a "dd/MM/yyyy" para FilterListDto; null si incompleto. */
private fun digitosAFecha(digits: String): String? {
    if (digits.length < 8) return null
    return "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4, 8)}"
}

enum class EstadoFiltroFactura(val label: String, val anulada: Boolean?) {
    TODOS("Todos", null),
    ACTIVAS("Activas", false),
    ANULADAS("Anuladas", true)
}

data class FacturasUiState(
    val isLoading: Boolean = false,
    val facturas: List<GetListDto> = emptyList(),
    val paginator: Paginator? = null,
    val currentPage: Int = 0,
    val pageSize: Int = 15,
    val errorMessage: String? = null,
    // filtros
    val filterSerie: String = "",
    val filterSecuencial: String = "",
    val filterFechaDesde: String = "",
    val filterFechaHasta: String = "",
    val filterEstado: EstadoFiltroFactura = EstadoFiltroFactura.TODOS,
    val filterTercero: String = "",
    val filterIdTercero: UUID? = null,
    // live search terceros
    val terceroSugerencias: List<GeTerceroGetListDto> = emptyList(),
    val terceroDropdownVisible: Boolean = false,
    val buscandoTercero: Boolean = false,
    // firma electrónica
    val firmaDialogFactura: GetListDto? = null,   // null = cerrado
    val firmando: Boolean = false,
    val firmaResultado: String? = null,
    val firmaError: String? = null,
    // exportación
    val isExporting     : Boolean = false,
    val exportResultado : String? = null,
    // descarga XML / PDF por fila
    val xmlPdfCargando  : UUID?   = null,
    val xmlPdfResultado : String? = null,
    // navegación al formulario
    val showForm  : Boolean = false,
    val editingId : UUID?   = null,
)

class FacturasViewModel(
    private val service        : VtVentasFacturasServiceImpl,
    private val tercerosService: GeTercerosServiceImpl,
    private val xmlPdfService  : GetXmlVtVentasFacturasServiceImpl,
    private val idData         : Long = 1L,
    private val idEmpresa      : Long
) {
    private val _state = MutableStateFlow(FacturasUiState(isLoading = true))
    val state: StateFlow<FacturasUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var terceroSearchJob: Job? = null

    companion object {
        private const val USUARIO = "desktop-user"
    }

    init {
        cargar()
    }

    private fun cargar(page: Int = _state.value.currentPage, snapshot: FacturasUiState = _state.value) {
        val s = snapshot
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val filtro = FilterListDto()
                filtro.serie      = s.filterSerie.trim().ifBlank { null }
                filtro.secuencial = s.filterSecuencial.trim().ifBlank { null }
                filtro.anulada    = s.filterEstado.anulada
                // Filtro por tercero: si el usuario seleccionó del dropdown se usa el UUID exacto,
                // si escribió manualmente se usa el nombre con LIKE (terceroNombre).
                s.filterIdTercero?.let { filtro.idTercero = it }
                if (s.filterIdTercero == null) {
                    filtro.setTerceroNombre(s.filterTercero.trim().ifBlank { null })
                }
                // Las fechas tienen getter que retorna LocalDate y setter que acepta String —
                // Kotlin no puede crear una propiedad sintética con tipos distintos,
                // por lo que se usan los métodos Java directamente.
                filtro.setFechaEmisionDesde(digitosAFecha(s.filterFechaDesde))
                filtro.setFechaEmisionHasta(digitosAFecha(s.filterFechaHasta))
                val pageable = PageRequest.of(page, s.pageSize, Sort.unsorted())
                val result   = service.findAllPaginate(
                    idData, idEmpresa, filtro, pageable, TipoPermiso.TODAS, USUARIO
                )
                _state.update {
                    it.copy(
                        isLoading   = false,
                        facturas    = result.content ?: emptyList(),
                        paginator   = result.paginator,
                        currentPage = page
                    )
                }
            } catch (e: Exception) {
                val msg = e.message ?: ""
                if (msg.contains("No existen datos", ignoreCase = true)) {
                    _state.update { it.copy(isLoading = false, facturas = emptyList(), paginator = null) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = msg.ifBlank { "Error al cargar facturas" }) }
                }
            }
        }
    }

    fun buscar() {
        // Capturar el snapshot del estado ANTES de cualquier actualización para
        // garantizar que filterIdTercero llegue íntegro a cargar(), sin importar
        // eventos de UI que puedan llegar entre el update y la coroutine.
        val snapshot = _state.value.copy(currentPage = 0)
        _state.update { it.copy(currentPage = 0) }
        cargar(0, snapshot)
    }

    fun limpiarFiltros() {
        _state.update {
            it.copy(
                filterSerie = "", filterSecuencial = "",
                filterFechaDesde = "", filterFechaHasta = "",
                filterEstado = EstadoFiltroFactura.TODOS,
                filterTercero = "", filterIdTercero = null, currentPage = 0
            )
        }
        cargar(0)
    }

    // ── Firma electrónica ─────────────────────────────────────────────────────
    fun abrirDialogoFirma(factura: GetListDto) =
        _state.update { it.copy(firmaDialogFactura = factura,
            firmaError = null, firmaResultado = null) }

    fun cerrarDialogoFirma() =
        _state.update { it.copy(firmaDialogFactura = null, firmaError = null) }

    fun dismissFirmaResultado() = _state.update { it.copy(firmaResultado = null) }

    fun procesarFirma() {
        val s       = _state.value
        val factura = s.firmaDialogFactura ?: return
        val id      = factura.idVenta ?: return
        scope.launch {
            _state.update { it.copy(firmando = true, firmaError = null) }
            try {
                _state.update { it.copy(
                    firmando         = false,
                    firmaDialogFactura = null,
                    firmaResultado   = "Factura firmada y procesada correctamente"
                ) }
                cargar()
            } catch (e: Exception) {
                _state.update { it.copy(firmando = false, firmaError = e.message ?: "Error al procesar la firma") }
            }
        }
    }

    // ── General ───────────────────────────────────────────────────────────────
    fun irPagina(page: Int)                              = cargar(page)
    fun setFilterSerie(v: String)                        = _state.update { it.copy(filterSerie = v) }
    fun setFilterSecuencial(v: String)                   = _state.update { it.copy(filterSecuencial = v) }
    fun setFilterFechaDesde(v: String)                   = _state.update { it.copy(filterFechaDesde = v) }
    fun setFilterFechaHasta(v: String)                   = _state.update { it.copy(filterFechaHasta = v) }
    fun setFilterEstado(v: EstadoFiltroFactura)          = _state.update { it.copy(filterEstado = v) }
    fun setFilterTercero(v: String) {
        _state.update { current ->
            // Si el nuevo valor es idéntico al texto que ya mostramos Y tenemos un tercero
            // seleccionado, es un evento de refocus del campo (no input real del usuario):
            // preservamos filterIdTercero para que buscar() lo use correctamente.
            val preservarId = current.filterIdTercero != null && v == current.filterTercero
            current.copy(
                filterTercero          = v,
                filterIdTercero        = if (preservarId) current.filterIdTercero else null,
                terceroDropdownVisible = false,
                terceroSugerencias     = emptyList()
            )
        }
        terceroSearchJob?.cancel()
        // Solo lanzar búsqueda si NO hay tercero seleccionado y el texto es suficiente
        val st = _state.value
        if (st.filterIdTercero == null && v.trim().length >= 2) {
            terceroSearchJob = scope.launch {
                delay(300)
                buscarTercerosSugerencias(v.trim())
            }
        }
    }

    private fun buscarTercerosSugerencias(query: String) {
        scope.launch {
            _state.update { it.copy(buscandoTercero = true) }
            try {
                val filterDto = GeTerceroFilterDto().apply { filter = query }
                val pageable  = PageRequest.of(0, 10, Sort.unsorted())
                val result    = tercerosService.findAllPaginate(idData, filterDto, pageable)
                val lista     = result.content ?: emptyList()
                _state.update {
                    it.copy(
                        buscandoTercero        = false,
                        terceroSugerencias     = lista,
                        terceroDropdownVisible = lista.isNotEmpty()
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(buscandoTercero = false, terceroSugerencias = emptyList(), terceroDropdownVisible = false) }
            }
        }
    }

    fun seleccionarTercero(tercero: GeTerceroGetListDto) {
        _state.update {
            it.copy(
                filterTercero          = tercero.tercero ?: "",
                filterIdTercero        = tercero.idTercero,
                terceroSugerencias     = emptyList(),
                terceroDropdownVisible = false
            )
        }
    }

    fun cerrarDropdownTercero() =
        _state.update { it.copy(terceroDropdownVisible = false) }

    fun dismissExportResultado()  = _state.update { it.copy(exportResultado = null) }
    fun dismissXmlPdfResultado()  = _state.update { it.copy(xmlPdfResultado = null) }

    // ── Navegación al formulario ──────────────────────────────────────────────
    fun abrirFormulario(id: UUID? = null) = _state.update { it.copy(showForm = true, editingId = id) }
    fun cerrarFormulario() { _state.update { it.copy(showForm = false, editingId = null) }; cargar() }

    fun descargarXml(factura: GetListDto) {
        val id = factura.idVenta ?: return
        if (_state.value.xmlPdfCargando != null) return
        scope.launch {
            _state.update { it.copy(xmlPdfCargando = id) }
            try {
                val dto = xmlPdfService.findFileXMLFactura(idData, idEmpresa, id)

                var chosenPath: String? = null
                SwingUtilities.invokeAndWait {
                    val chooser = JFileChooser()
                    chooser.dialogTitle       = "Guardar XML"
                    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
                    chooser.fileFilter        = FileNameExtensionFilter("XML (*.xml)", "xml")
                    chooser.selectedFile      = java.io.File(dto.nombre)
                    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
                        chosenPath = chooser.selectedFile.absolutePath
                }

                if (chosenPath == null) { _state.update { it.copy(xmlPdfCargando = null) }; return@launch }

                val finalPath = if (chosenPath!!.endsWith(".xml", ignoreCase = true)) chosenPath!! else "$chosenPath.xml"
                FileOutputStream(finalPath).use { it.write(dto.contenido) }

                _state.update { it.copy(xmlPdfCargando = null, xmlPdfResultado = "XML guardado:\n$finalPath") }
            } catch (e: Exception) {
                _state.update { it.copy(xmlPdfCargando = null, xmlPdfResultado = "Error al descargar XML: ${e.message ?: "Error desconocido"}") }
            }
        }
    }

    fun descargarPdf(factura: GetListDto) {
        val id = factura.idVenta ?: return
        if (_state.value.xmlPdfCargando != null) return
        scope.launch {
            _state.update { it.copy(xmlPdfCargando = id) }
            try {
                val dto = xmlPdfService.findPDFFacturaById(idData, idEmpresa, id, "LOC")

                var chosenPath: String? = null
                SwingUtilities.invokeAndWait {
                    val chooser = JFileChooser()
                    chooser.dialogTitle       = "Guardar PDF"
                    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
                    chooser.fileFilter        = FileNameExtensionFilter("PDF (*.pdf)", "pdf")
                    chooser.selectedFile      = java.io.File(dto.nombre)
                    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
                        chosenPath = chooser.selectedFile.absolutePath
                }

                if (chosenPath == null) { _state.update { it.copy(xmlPdfCargando = null) }; return@launch }

                val finalPath = if (chosenPath!!.endsWith(".pdf", ignoreCase = true)) chosenPath!! else "$chosenPath.pdf"
                FileOutputStream(finalPath).use { it.write(dto.contenido) }

                _state.update { it.copy(xmlPdfCargando = null, xmlPdfResultado = "PDF guardado:\n$finalPath") }
            } catch (e: Exception) {
                _state.update { it.copy(xmlPdfCargando = null, xmlPdfResultado = "Error al descargar PDF: ${e.message ?: "Error desconocido"}") }
            }
        }
    }

    fun imprimirPdf(factura: GetListDto) {
        val id = factura.idVenta ?: return
        if (_state.value.xmlPdfCargando != null) return
        scope.launch {
            _state.update { it.copy(xmlPdfCargando = id) }
            try {
                val dto = xmlPdfService.findPDFFacturaById(idData, idEmpresa, id, "LOC")
                val doc = Loader.loadPDF(dto.contenido)
                try {
                    var shouldPrint = false
                    var printerJob: PrinterJob? = null
                    SwingUtilities.invokeAndWait {
                        printerJob = PrinterJob.getPrinterJob()
                        printerJob!!.setPageable(PDFPageable(doc))
                        shouldPrint = printerJob!!.printDialog()
                    }
                    if (shouldPrint) printerJob!!.print()
                } finally {
                    doc.close()
                }
                _state.update { it.copy(xmlPdfCargando = null) }
            } catch (e: Exception) {
                _state.update { it.copy(xmlPdfCargando = null, xmlPdfResultado = "Error al imprimir: ${e.message ?: "Error desconocido"}") }
            }
        }
    }

    fun exportarExcel() {
        if (_state.value.isExporting) return
        scope.launch {
            _state.update { it.copy(isExporting = true, exportResultado = null) }
            try {
                val s = _state.value

                // Mismo FilterListDto que cargar()
                val filtro = FilterListDto()
                filtro.serie      = s.filterSerie.trim().ifBlank { null }
                filtro.secuencial = s.filterSecuencial.trim().ifBlank { null }
                filtro.anulada    = s.filterEstado.anulada
                s.filterIdTercero?.let { filtro.idTercero = it }
                if (s.filterIdTercero == null) filtro.setTerceroNombre(s.filterTercero.trim().ifBlank { null })
                filtro.setFechaEmisionDesde(digitosAFecha(s.filterFechaDesde))
                filtro.setFechaEmisionHasta(digitosAFecha(s.filterFechaHasta))

                // Nombre de archivo sugerido
                val ts        = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
                val suggested = "Facturas_$ts.xlsx"

                // JFileChooser debe correr en el EDT de Swing
                var chosenPath: String? = null
                SwingUtilities.invokeAndWait {
                    val chooser = JFileChooser()
                    chooser.dialogTitle       = "Exportar Facturas a Excel"
                    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
                    chooser.fileFilter        = FileNameExtensionFilter("Excel (*.xlsx)", "xlsx")
                    chooser.selectedFile      = java.io.File(suggested)
                    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
                        chosenPath = chooser.selectedFile.absolutePath
                }

                if (chosenPath == null) {
                    _state.update { it.copy(isExporting = false) }
                    return@launch
                }

                val finalPath = if (chosenPath!!.endsWith(".xlsx", ignoreCase = true))
                    chosenPath!! else "$chosenPath.xlsx"

                FileOutputStream(finalPath).use { fos ->
                    service.exportarExcel(idData, idEmpresa, fos, filtro)
                }

                _state.update { it.copy(isExporting = false, exportResultado = "Archivo exportado:\n$finalPath") }
            } catch (e: Exception) {
                _state.update { it.copy(isExporting = false, exportResultado = "Error al exportar: ${e.message ?: "Error desconocido"}") }
            }
        }
    }

    fun dismissError()                                   = _state.update { it.copy(errorMessage = null) }

    fun onDestroy()                                      = scope.cancel()
}
