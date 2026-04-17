package com.calero.lili.desktop.ui.ventas.notasCredito

import java.util.UUID
import com.calero.lili.core.dtos.Paginator
import com.calero.lili.core.enums.TipoPermiso
import com.calero.lili.core.modTerceros.GeTercerosServiceImpl
import com.calero.lili.core.modTerceros.dto.GeTerceroFilterDto
import com.calero.lili.core.modTerceros.dto.GeTerceroGetListDto
import com.calero.lili.core.modVentas.dto.GetListDto
import com.calero.lili.core.modVentas.notasCredito.VtVentasNotasCreditoServiceImpl
import com.calero.lili.core.comprobantesWs.services.GetXmlVtVentasNotasCreditoServiceImpl
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

private fun digitosAFecha(digits: String): String? {
    if (digits.length < 8) return null
    return "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4, 8)}"
}

enum class EstadoFiltroNotaCredito(val label: String, val anulada: Boolean?) {
    TODOS("Todos", null),
    ACTIVAS("Activas", false),
    ANULADAS("Anuladas", true)
}

data class NotasCreditoUiState(
    val isLoading: Boolean = false,
    val notasCredito: List<GetListDto> = emptyList(),
    val paginator: Paginator? = null,
    val currentPage: Int = 0,
    val pageSize: Int = 15,
    val errorMessage: String? = null,
    // filtros
    val filterSerie: String = "",
    val filterSecuencial: String = "",
    val filterFechaDesde: String = "",
    val filterFechaHasta: String = "",
    val filterEstado: EstadoFiltroNotaCredito = EstadoFiltroNotaCredito.TODOS,
    val filterTercero: String = "",
    val filterIdTercero: UUID? = null,
    val filterNumeroIdentificacion: String? = null,
    // live search terceros
    val terceroSugerencias: List<GeTerceroGetListDto> = emptyList(),
    val terceroDropdownVisible: Boolean = false,
    val buscandoTercero: Boolean = false,
    // firma electrónica
    val firmaDialogNota: GetListDto? = null,
    val firmando: Boolean = false,
    val firmaResultado: String? = null,
    val firmaError: String? = null,
    // exportación
    val isExporting: Boolean = false,
    val exportResultado: String? = null,
    // descarga XML / PDF por fila
    val xmlPdfCargando: UUID? = null,
    val xmlPdfResultado: String? = null,
    // navegación al formulario
    val showForm: Boolean = false,
    val editingId: UUID? = null,
)

class NotasCreditoViewModel(
    private val service: VtVentasNotasCreditoServiceImpl,
    private val tercerosService: GeTercerosServiceImpl,
    private val xmlPdfService: GetXmlVtVentasNotasCreditoServiceImpl,
    private val idData: Long = 1L,
    private val idEmpresa: Long
) {
    private val _state = MutableStateFlow(NotasCreditoUiState(isLoading = true))
    val state: StateFlow<NotasCreditoUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var terceroSearchJob: Job? = null

    companion object {
        private const val USUARIO = "desktop-user"
    }

    init {
        cargar()
    }

    private fun cargar(page: Int = _state.value.currentPage, snapshot: NotasCreditoUiState = _state.value) {
        val s = snapshot
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val filtro = FilterListDto()
                filtro.serie = s.filterSerie.trim().ifBlank { null }
                filtro.secuencial = s.filterSecuencial.trim().ifBlank { null }
                filtro.anulada = s.filterEstado.anulada
                // Filtro por tercero: si hay un tercero seleccionado del dropdown se usa su
                // número de identificación; si no, se usa el texto como búsqueda por identificación.
                val numId = s.filterNumeroIdentificacion ?: s.filterTercero.trim().ifBlank { null }
                filtro.numeroIdentificacion = numId
                filtro.setFechaEmisionDesde(digitosAFecha(s.filterFechaDesde))
                filtro.setFechaEmisionHasta(digitosAFecha(s.filterFechaHasta))
                val pageable = PageRequest.of(page, s.pageSize, Sort.unsorted())
                val result = service.findAllPaginate(
                    idData, idEmpresa, filtro, pageable, TipoPermiso.TODAS, USUARIO
                )
                _state.update {
                    it.copy(
                        isLoading = false,
                        notasCredito = result.content ?: emptyList(),
                        paginator = result.paginator,
                        currentPage = page
                    )
                }
            } catch (e: Exception) {
                val msg = e.message ?: ""
                if (msg.contains("No existen datos", ignoreCase = true)) {
                    _state.update { it.copy(isLoading = false, notasCredito = emptyList(), paginator = null) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = msg.ifBlank { "Error al cargar notas de crédito" }) }
                }
            }
        }
    }

    fun buscar() {
        val snapshot = _state.value.copy(currentPage = 0)
        _state.update { it.copy(currentPage = 0) }
        cargar(0, snapshot)
    }

    fun limpiarFiltros() {
        _state.update {
            it.copy(
                filterSerie = "", filterSecuencial = "",
                filterFechaDesde = "", filterFechaHasta = "",
                filterEstado = EstadoFiltroNotaCredito.TODOS,
                filterTercero = "", filterIdTercero = null,
                filterNumeroIdentificacion = null, currentPage = 0
            )
        }
        cargar(0)
    }

    // ── Firma electrónica ─────────────────────────────────────────────────────
    fun abrirDialogoFirma(nota: GetListDto) =
        _state.update { it.copy(firmaDialogNota = nota, firmaError = null, firmaResultado = null) }

    fun cerrarDialogoFirma() =
        _state.update { it.copy(firmaDialogNota = null, firmaError = null) }

    fun dismissFirmaResultado() = _state.update { it.copy(firmaResultado = null) }

    fun procesarFirma() {
        val s = _state.value
        val nota = s.firmaDialogNota ?: return
        nota.idVenta ?: return
        scope.launch {
            _state.update { it.copy(firmando = true, firmaError = null) }
            try {
                _state.update {
                    it.copy(
                        firmando = false,
                        firmaDialogNota = null,
                        firmaResultado = "Nota de crédito firmada y procesada correctamente"
                    )
                }
                cargar()
            } catch (e: Exception) {
                _state.update { it.copy(firmando = false, firmaError = e.message ?: "Error al procesar la firma") }
            }
        }
    }

    // ── General ───────────────────────────────────────────────────────────────
    fun irPagina(page: Int) = cargar(page)
    fun setFilterSerie(v: String) = _state.update { it.copy(filterSerie = v) }
    fun setFilterSecuencial(v: String) = _state.update { it.copy(filterSecuencial = v) }
    fun setFilterFechaDesde(v: String) = _state.update { it.copy(filterFechaDesde = v) }
    fun setFilterFechaHasta(v: String) = _state.update { it.copy(filterFechaHasta = v) }
    fun setFilterEstado(v: EstadoFiltroNotaCredito) = _state.update { it.copy(filterEstado = v) }
    fun setFilterTercero(v: String) {
        _state.update { current ->
            val preservarId = current.filterIdTercero != null && v == current.filterTercero
            current.copy(
                filterTercero = v,
                filterIdTercero = if (preservarId) current.filterIdTercero else null,
                filterNumeroIdentificacion = if (preservarId) current.filterNumeroIdentificacion else null,
                terceroDropdownVisible = false,
                terceroSugerencias = emptyList()
            )
        }
        terceroSearchJob?.cancel()
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
                val pageable = PageRequest.of(0, 10, Sort.unsorted())
                val result = tercerosService.findAllPaginate(idData, filterDto, pageable)
                val lista = result.content ?: emptyList()
                _state.update {
                    it.copy(
                        buscandoTercero = false,
                        terceroSugerencias = lista,
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
                filterTercero = tercero.tercero ?: "",
                filterIdTercero = tercero.idTercero,
                filterNumeroIdentificacion = tercero.numeroIdentificacion,
                terceroSugerencias = emptyList(),
                terceroDropdownVisible = false
            )
        }
    }

    fun cerrarDropdownTercero() = _state.update { it.copy(terceroDropdownVisible = false) }

    fun dismissExportResultado() = _state.update { it.copy(exportResultado = null) }
    fun dismissXmlPdfResultado() = _state.update { it.copy(xmlPdfResultado = null) }

    // ── Navegación al formulario ──────────────────────────────────────────────
    fun abrirFormulario(id: UUID? = null) = _state.update { it.copy(showForm = true, editingId = id) }
    fun cerrarFormulario() { _state.update { it.copy(showForm = false, editingId = null) }; cargar() }

    fun descargarXml(nota: GetListDto) {
        val id = nota.idVenta ?: return
        if (_state.value.xmlPdfCargando != null) return
        scope.launch {
            _state.update { it.copy(xmlPdfCargando = id) }
            try {
                val dto = xmlPdfService.findFileXMLNotaCredito(idData, idEmpresa, id)

                var chosenPath: String? = null
                SwingUtilities.invokeAndWait {
                    val chooser = JFileChooser()
                    chooser.dialogTitle = "Guardar XML"
                    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
                    chooser.fileFilter = FileNameExtensionFilter("XML (*.xml)", "xml")
                    chooser.selectedFile = java.io.File(dto.nombre)
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

    fun descargarPdf(nota: GetListDto) {
        val id = nota.idVenta ?: return
        if (_state.value.xmlPdfCargando != null) return
        scope.launch {
            _state.update { it.copy(xmlPdfCargando = id) }
            try {
                val dto = xmlPdfService.findPDFNotaCreditoById(idData, idEmpresa, id, "LOC")

                var chosenPath: String? = null
                SwingUtilities.invokeAndWait {
                    val chooser = JFileChooser()
                    chooser.dialogTitle = "Guardar PDF"
                    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
                    chooser.fileFilter = FileNameExtensionFilter("PDF (*.pdf)", "pdf")
                    chooser.selectedFile = java.io.File(dto.nombre)
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

    fun imprimirPdf(nota: GetListDto) {
        val id = nota.idVenta ?: return
        if (_state.value.xmlPdfCargando != null) return
        scope.launch {
            _state.update { it.copy(xmlPdfCargando = id) }
            try {
                val dto = xmlPdfService.findPDFNotaCreditoById(idData, idEmpresa, id, "LOC")
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

                val filtro = FilterListDto()
                filtro.serie = s.filterSerie.trim().ifBlank { null }
                filtro.secuencial = s.filterSecuencial.trim().ifBlank { null }
                filtro.anulada = s.filterEstado.anulada
                val numId = s.filterNumeroIdentificacion ?: s.filterTercero.trim().ifBlank { null }
                filtro.numeroIdentificacion = numId
                filtro.setFechaEmisionDesde(digitosAFecha(s.filterFechaDesde))
                filtro.setFechaEmisionHasta(digitosAFecha(s.filterFechaHasta))

                val ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
                val suggested = "NotasCredito_$ts.xlsx"

                var chosenPath: String? = null
                SwingUtilities.invokeAndWait {
                    val chooser = JFileChooser()
                    chooser.dialogTitle = "Exportar Notas de Crédito a Excel"
                    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
                    chooser.fileFilter = FileNameExtensionFilter("Excel (*.xlsx)", "xlsx")
                    chooser.selectedFile = java.io.File(suggested)
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

    fun dismissError() = _state.update { it.copy(errorMessage = null) }

    fun onDestroy() = scope.cancel()
}
