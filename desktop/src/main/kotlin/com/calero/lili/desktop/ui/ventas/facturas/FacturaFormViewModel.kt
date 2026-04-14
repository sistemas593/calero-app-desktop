package com.calero.lili.desktop.ui.ventas.facturas

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.calero.lili.core.dtos.FormasPagoDto
import com.calero.lili.core.dtos.InformacionAdicionalDto
import com.calero.lili.core.enums.FormaPago
import com.calero.lili.core.enums.FormatoDocumento
import com.calero.lili.core.enums.Liquidar
import com.calero.lili.core.enums.TipoIdentificacion
import com.calero.lili.core.enums.TipoIngreso
import com.calero.lili.core.enums.TipoPermiso
import com.calero.lili.core.comprobantesWs.services.GetXmlVtVentasFacturasServiceImpl
import com.calero.lili.core.modAdminEmpresasSeries.AdEmpresasSeriesServiceImpl
import com.calero.lili.core.modAdminEmpresasSeries.dto.AdEmpresaSerieFacturaDto
import com.calero.lili.core.modComprasItems.GeItemsServiceImpl
import com.calero.lili.core.modComprasItems.dto.GeItemGetListDto
import com.calero.lili.core.modComprasItems.dto.GeItemListFilterDto
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestoItemsServiceImpl
import com.calero.lili.core.modComprasItemsImpuesto.dto.GeImpuestoResponseDto
import com.calero.lili.core.modContabilidad.modCentroCostos.CnCentroCostosServiceImpl
import com.calero.lili.core.modContabilidad.modCentroCostos.dto.CentroCostosDtoResponse
import com.calero.lili.core.tablas.tbFormasPagoSri.TbFormaPagoSriGetOneDto
import com.calero.lili.core.tablas.tbFormasPagoSri.TbFormasPagoSriServiceImpl
import com.calero.lili.core.dtos.FilterDto
import com.calero.lili.core.modTerceros.GeTercerosServiceImpl
import com.calero.lili.core.modTerceros.dto.GeTerceroFilterDto
import com.calero.lili.core.modTerceros.dto.GeTerceroGetListDto
import com.calero.lili.core.modVentas.facturas.VtVentasFacturasServiceImpl
import com.calero.lili.core.modVentas.facturas.dto.CreationFacturaRequestDto
import com.calero.lili.core.modVentas.facturas.dto.FilterListDto
import java.awt.print.PrinterJob
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
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

// ── Modelo UI de una fila de detalle ─────────────────────────────────────────
data class DetalleItemUi(
    val key: UUID = UUID.randomUUID(),
    val itemOrden: Int = 1,
    val idItem: UUID? = null,
    val codigoPrincipal: String = "",
    val codigoAuxiliar: String = "",
    val codigoBarras: String = "",
    val unidadMedida: String = "",
    val descripcion: String = "",
    val precioUnitario: BigDecimal = BigDecimal.ZERO,
    val cantidad: BigDecimal = BigDecimal.ONE,
    val dsctoItem: BigDecimal = BigDecimal.ZERO,
    val descuento: BigDecimal = BigDecimal.ZERO,
    val subtotalItem: BigDecimal = BigDecimal.ZERO,
    val impuesto: GeImpuestoResponseDto? = null,
    val idCentroCostos: UUID? = null
)

// ── Modelo UI de un campo adicional ──────────────────────────────────────────
data class CampoAdicionalUi(
    val key: UUID = UUID.randomUUID(),
    val nombre: String = "",
    val valor: String = ""
)

// ── Modelo UI de una fila de forma de pago SRI ───────────────────────────────
data class FormaPagoSriUi(
    val key: UUID = UUID.randomUUID(),
    val formaPago: String = "01",
    val descripcion: String = "",
    val total: BigDecimal = BigDecimal.ZERO,
    val plazo: String = "",
    val unidadTiempo: String = ""
)

// ── Estado del formulario ─────────────────────────────────────────────────────
data class FacturaFormUiState(
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    // Tab 1 — Encabezado
    val sucursal: String = "",
    val fechaEmision: String = "",
    val serie: String = "",
    val secuencial: String = "",
    val tipoIngreso: TipoIngreso = TipoIngreso.VL,
    val codigoDocumento: String = "18",
    val concepto: String = "",
    val ambiente: Int = 1,
    val cuentaPorCobrar: Boolean = true,
    val liquidar: Liquidar = Liquidar.S,
    // Tab 2 — Cliente
    val idTercero: UUID? = null,
    val terceroNombre: String = "",
    val terceroQuery: String = "",
    val tipoIdentificacion: TipoIdentificacion = TipoIdentificacion.R,
    val numeroIdentificacion: String = "",
    val email: String = "",
    val relacionado: String = "N",
    val direccion: String = "",
    val telefonos: String = "",
    val terceroSugerencias: List<GeTerceroGetListDto> = emptyList(),
    val terceroDropdownVisible: Boolean = false,
    val buscandoTercero: Boolean = false,
    // Tab 3 — Detalle
    val detalle: List<DetalleItemUi> = emptyList(),
    val impuestosDisponibles: List<GeImpuestoResponseDto> = emptyList(),
    val centrosCostosDisponibles: List<CentroCostosDtoResponse> = emptyList(),
    // Tab 4 — Pago y Totales
    val formaPago: FormaPago = FormaPago.CO,
    val diasCredito: Int = 0,
    val formasPagoSri: List<FormaPagoSriUi> = emptyList(),
    val subtotal: BigDecimal = BigDecimal.ZERO,
    val totalDescuento: BigDecimal = BigDecimal.ZERO,
    val totalImpuesto: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal = BigDecimal.ZERO,
    val subtotal15: BigDecimal = BigDecimal.ZERO,
    val subtotal5: BigDecimal = BigDecimal.ZERO,
    val subtotalTarifaEspecial: BigDecimal = BigDecimal.ZERO,
    val subtotal0: BigDecimal = BigDecimal.ZERO,
    val subtotalNoObjeto: BigDecimal = BigDecimal.ZERO,
    val subtotalExento: BigDecimal = BigDecimal.ZERO,
    val valorIce: BigDecimal = BigDecimal.ZERO,
    val iva15: BigDecimal = BigDecimal.ZERO,
    val iva5: BigDecimal = BigDecimal.ZERO,
    val ivaTarifaEspecial: BigDecimal = BigDecimal.ZERO,
    val propina10: Boolean = false,
    // Diálogo item
    val showItemDialog: Boolean = false,
    val itemDialogKeyDestino: UUID? = null,
    val itemDialogQuery: String = "",
    val itemDialogResultados: List<GeItemGetListDto> = emptyList(),
    val itemDialogBuscando: Boolean = false,
    // Diálogo formas de pago SRI
    val formasPagoSriDisponibles: List<TbFormaPagoSriGetOneDto> = emptyList(),
    val showFormaPagoDialog: Boolean = false,
    val dialogFpCodigo: String = "",
    val dialogFpDescripcion: String = "",
    val dialogFpValor: BigDecimal = BigDecimal.ZERO,
    val dialogFpPlazo: String = "",
    val dialogFpUnidadTiempo: String = "Días",
    // Campos adicionales
    val camposAdicionales: List<CampoAdicionalUi> = emptyList(),
    val showCampoAdicionalDialog: Boolean = false,
    val dialogCaNombre: String = "",
    val dialogCaValor: String = "",
    // Series disponibles para facturas
    val seriesDisponibles: List<AdEmpresaSerieFacturaDto> = emptyList(),
    // PDF viewer — se muestra tras guardar
    val pdfBytes      : ByteArray?  = null,
    val pdfLoading    : Boolean     = false,
    val pdfNombre     : String      = "",
    val showPdfViewer : Boolean     = false
)

// ── ViewModel ─────────────────────────────────────────────────────────────────
class FacturaFormViewModel(
    private val service: VtVentasFacturasServiceImpl,
    private val tercerosService: GeTercerosServiceImpl,
    private val itemsService: GeItemsServiceImpl,
    private val impuestosService: GeImpuestoItemsServiceImpl,
    private val centroCostosService: CnCentroCostosServiceImpl,
    private val formasPagoSriService: TbFormasPagoSriServiceImpl,
    private val seriesService: AdEmpresasSeriesServiceImpl,
    private val xmlPdfService: GetXmlVtVentasFacturasServiceImpl,
    private val idFactura: UUID? = null,
    private val idData: Long = 1L,
    private val idEmpresa: Long
) {
    private val _state = MutableStateFlow(FacturaFormUiState(isEditMode = idFactura != null, fechaEmision = fechaHoy()))
    val state: StateFlow<FacturaFormUiState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var terceroSearchJob: Job? = null
    private var itemDialogSearchJob: Job? = null

    companion object {
        private const val USUARIO = "desktop-user"
        private val FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        fun fechaHoy(): String = LocalDate.now().format(FORMATO_FECHA)
    }

    init {
        cargarDatosIniciales()
    }

    // ── Carga inicial ─────────────────────────────────────────────────────────
    private fun cargarDatosIniciales() {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val impuestos        = runCatching { impuestosService.findAll() }.getOrElse { emptyList() }
                val centrosCostos   = runCatching { centroCostosService.findAll(idData, idEmpresa) }.getOrElse { emptyList() }
                @Suppress("UNCHECKED_CAST")
                val formasPagoSri: List<TbFormaPagoSriGetOneDto> = runCatching {
                    (formasPagoSriService.findAllPaginate(FilterDto(), org.springframework.data.domain.PageRequest.of(0, 100, Sort.unsorted())).content as? List<TbFormaPagoSriGetOneDto>) ?: emptyList()
                }.getOrElse { emptyList() }
                val series = runCatching { seriesService.findSeriesParaFacturas(idData, idEmpresa) }.getOrElse { emptyList() }
                _state.update { it.copy(impuestosDisponibles = impuestos, centrosCostosDisponibles = centrosCostos, formasPagoSriDisponibles = formasPagoSri, seriesDisponibles = series) }
                if (idFactura != null) cargarFactura(idFactura, impuestos, formasPagoSri)
                else _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar datos") }
            }
        }
    }

    private fun cargarFactura(id: UUID, impuestos: List<GeImpuestoResponseDto>, formasPagoDisp: List<TbFormaPagoSriGetOneDto> = emptyList()) {
        scope.launch {
            try {
                val dto     = service.findById(idData, idEmpresa, id, FilterListDto(), TipoPermiso.TODAS, USUARIO)
                val tercero = runCatching { tercerosService.findById(idData, dto.idTercero, idEmpresa) }.getOrNull()

                val detalleUi = dto.detalle?.mapIndexed { idx, d ->
                    val primerImp = d.impuesto?.firstOrNull()
                    val impUi = primerImp?.let { pi ->
                        impuestos.find { it.codigo == pi.codigo && it.codigoPorcentaje == pi.codigoPorcentaje }
                    }
                    DetalleItemUi(
                        itemOrden      = idx + 1,
                        idItem         = d.idItem,
                        codigoPrincipal = d.codigoPrincipal ?: "",
                        codigoAuxiliar  = d.codigoAuxiliar ?: "",
                        codigoBarras    = d.codigoBarras ?: "",
                        unidadMedida    = d.unidadMedida ?: "",
                        descripcion     = d.descripcion ?: "",
                        precioUnitario  = d.precioUnitario ?: BigDecimal.ZERO,
                        cantidad        = d.cantidad ?: BigDecimal.ONE,
                        dsctoItem       = d.dsctoItem ?: BigDecimal.ZERO,
                        descuento       = d.descuento ?: BigDecimal.ZERO,
                        subtotalItem    = d.subTotalItem ?: BigDecimal.ZERO,
                        impuesto        = impUi,
                        idCentroCostos  = d.centroCostos?.idCentroCostos
                    )
                } ?: emptyList()

                _state.update { s -> s.copy(
                    isLoading           = false,
                    sucursal            = dto.sucursal ?: "",
                    fechaEmision        = fechaHoy(),
                    serie               = dto.serie ?: "",
                    secuencial          = dto.secuencial ?: "",
                    tipoIngreso         = TipoIngreso.VL,
                    codigoDocumento     = dto.codigoDocumento ?: "18",
                    ambiente            = dto.ambiente ?: 1,
                    cuentaPorCobrar     = true,
                    liquidar            = Liquidar.S,
                    idTercero           = dto.idTercero,
                    terceroNombre       = tercero?.tercero ?: "",
                    terceroQuery        = tercero?.tercero ?: "",
                    tipoIdentificacion  = tercero?.tipoIdentificacion?.let { runCatching { TipoIdentificacion.valueOf(it) }.getOrNull() } ?: TipoIdentificacion.R,
                    numeroIdentificacion = tercero?.numeroIdentificacion ?: "",
                    email               = dto.email ?: "",
                    direccion           = tercero?.direccion ?: "",
                    telefonos           = tercero?.telefonos ?: "",
                    relacionado         = dto.relacionado ?: "N",
                    detalle             = detalleUi,
                    formaPago           = dto.formaPago ?: FormaPago.CO,
                    diasCredito         = dto.diasCredito ?: 0,
                    formasPagoSri       = dto.formasPagoSri?.map {
                        val desc = formasPagoDisp.find { fp -> fp.codigoFormaPagoSri == it.formaPago }?.formaPagoSri ?: it.formaPago ?: ""
                        FormaPagoSriUi(formaPago = it.formaPago ?: "01", descripcion = desc, total = it.total ?: BigDecimal.ZERO, plazo = it.plazo ?: "", unidadTiempo = it.unidadTiempo ?: "")
                    }?.takeIf { it.isNotEmpty() } ?: emptyList(),
                    camposAdicionales   = dto.informacionAdicional?.map {
                        CampoAdicionalUi(nombre = it.nombre ?: "", valor = it.valor ?: "")
                    } ?: emptyList(),
                    subtotal            = dto.subtotal ?: BigDecimal.ZERO,
                    totalDescuento      = dto.totalDescuento ?: BigDecimal.ZERO,
                    totalImpuesto       = dto.totalImpuesto ?: BigDecimal.ZERO,
                    total               = dto.total ?: BigDecimal.ZERO
                )}
                recalcularTotales(detalleUi)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar la factura") }
            }
        }
    }

    // ── Guardar ───────────────────────────────────────────────────────────────
    fun guardar(onGuardado: () -> Unit) {
        val s = _state.value
        if (s.idTercero == null)  { _state.update { it.copy(errorMessage = "Debe seleccionar un cliente") };     return }
        if (s.serie.isBlank())    { _state.update { it.copy(errorMessage = "La serie es requerida") };           return }
        if (s.secuencial.isBlank()){ _state.update { it.copy(errorMessage = "El secuencial es requerido") };     return }
        if (s.fechaEmision.isBlank()){ _state.update { it.copy(errorMessage = "La fecha de emisión es requerida") }; return }
        if (s.detalle.isEmpty())  { _state.update { it.copy(errorMessage = "Debe agregar al menos un item") };   return }
        if (s.numeroIdentificacion == "9999999999" && s.total > BigDecimal("50")) {
            _state.update { it.copy(errorMessage = "El adquiriente 'Consumidor Final' no puede tener un total mayor a \$50.00. Por favor seleccione otro adquiriente.") }
            return
        }

        scope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val request = buildRequest(s)
                val facturaId: UUID = if (idFactura == null) {
                    service.create(idData, idEmpresa, request, USUARIO, "LOC").idDocumento
                } else {
                    UUID.fromString(
                        service.update(idData, idEmpresa, idFactura, request, FilterListDto(), TipoPermiso.TODAS, USUARIO).id
                    )
                }

                // Abrir dialog con spinner de forma inmediata
                _state.update { it.copy(isSaving = false, showPdfViewer = true, pdfLoading = true) }
                try {
                    val archivo = xmlPdfService.findPDFFacturaById(idData, idEmpresa, facturaId, "LOC")
                    _state.update { it.copy(pdfLoading = false, pdfBytes = archivo.contenido, pdfNombre = archivo.nombre) }
                } catch (pdfEx: Exception) {
                    // PDF no disponible (ej: documento aún no autorizado)
                    val msg = if (idFactura == null) "Factura creada correctamente" else "Factura actualizada correctamente"
                    _state.update { it.copy(pdfLoading = false, showPdfViewer = false, successMessage = msg) }
                    delay(900)
                    onGuardado()
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, errorMessage = e.message ?: "Error al guardar") }
            }
        }
    }

    fun dismissPdfViewer() {
        _state.update { it.copy(showPdfViewer = false, pdfBytes = null, pdfNombre = "") }
    }

    fun descargarPdfActual() {
        val s = _state.value
        val bytes  = s.pdfBytes ?: return
        val nombre = s.pdfNombre.ifBlank { "factura" }
        scope.launch {
            try {
                var chosenPath: String? = null
                SwingUtilities.invokeAndWait {
                    val chooser = JFileChooser()
                    chooser.dialogTitle       = "Guardar PDF"
                    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
                    chooser.fileFilter        = FileNameExtensionFilter("PDF (*.pdf)", "pdf")
                    chooser.selectedFile      = java.io.File("$nombre.pdf")
                    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
                        chosenPath = chooser.selectedFile.absolutePath
                }
                if (chosenPath == null) return@launch
                val finalPath = if (chosenPath!!.endsWith(".pdf", ignoreCase = true)) chosenPath!! else "$chosenPath.pdf"
                FileOutputStream(finalPath).use { it.write(bytes) }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Error al guardar PDF: ${e.message}") }
            }
        }
    }

    fun imprimirPdfActual() {
        val bytes = _state.value.pdfBytes ?: return
        scope.launch {
            try {
                var shouldPrint = false
                var printerJob: PrinterJob? = null
                val doc = Loader.loadPDF(bytes)
                try {
                    SwingUtilities.invokeAndWait {
                        printerJob = PrinterJob.getPrinterJob()
                        printerJob!!.setPageable(PDFPageable(doc))
                        shouldPrint = printerJob!!.printDialog()
                    }
                    if (shouldPrint) printerJob!!.print()
                } finally {
                    doc.close()
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Error al imprimir: ${e.message}") }
            }
        }
    }

    private fun buildRequest(s: FacturaFormUiState): CreationFacturaRequestDto {
        val detalleRequest = s.detalle.mapIndexed { idx, d ->
            val imp = d.impuesto
            val impList = if (imp != null) {
                val base = d.subtotalItem
                val valorImp = base.multiply(imp.tarifa ?: BigDecimal.ZERO)
                    .divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
                listOf(CreationFacturaRequestDto.DetailDto.Impuestos(
                    imp.codigo, imp.codigoPorcentaje, imp.tarifa, base, valorImp
                ))
            } else emptyList()

            CreationFacturaRequestDto.DetailDto().apply {
                idItem          = d.idItem
                itemOrden       = idx + 1
                codigoPrincipal = d.codigoPrincipal
                codigoAuxiliar  = d.codigoAuxiliar.ifBlank { null }
                codigoBarras    = d.codigoBarras.ifBlank { null }
                descripcion     = d.descripcion
                unidadMedida    = d.unidadMedida.ifBlank { null }
                precioUnitario  = d.precioUnitario
                cantidad        = d.cantidad
                dsctoItem       = d.dsctoItem
                descuento       = d.descuento
                subtotalItem    = d.subtotalItem
                idCentroCostos  = d.idCentroCostos
                impuesto        = impList
            }
        }

        // Global valores — agrupado por codigo+codigoPorcentaje
        data class ValKey(val codigo: String, val codPct: String, val tarifa: BigDecimal)
        val baseAcum  = mutableMapOf<ValKey, BigDecimal>()
        val valorAcum = mutableMapOf<ValKey, BigDecimal>()
        s.detalle.forEach { d ->
            val imp = d.impuesto ?: return@forEach
            val key = ValKey(imp.codigo ?: "", imp.codigoPorcentaje ?: "", imp.tarifa ?: BigDecimal.ZERO)
            val base = d.subtotalItem
            val v    = base.multiply(imp.tarifa ?: BigDecimal.ZERO).divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
            baseAcum[key]  = (baseAcum[key]  ?: BigDecimal.ZERO).add(base)
            valorAcum[key] = (valorAcum[key] ?: BigDecimal.ZERO).add(v)
        }
        val valoresRequest = baseAcum.entries.map { (k, base) ->
            CreationFacturaRequestDto.ValoresDto(k.codigo, k.codPct, k.tarifa, base, valorAcum[k] ?: BigDecimal.ZERO)
        }

        val formasPagoRequest = s.formasPagoSri.map { fp ->
            FormasPagoDto().apply {
                formaPago   = fp.formaPago
                total       = fp.total
                plazo       = fp.plazo
                unidadTiempo = fp.unidadTiempo
            }
        }

        return CreationFacturaRequestDto().apply {
            sucursal          = s.sucursal
            formatoDocumento  = FormatoDocumento.E
            serie             = s.serie
            secuencial        = s.secuencial
            fechaEmision      = s.fechaEmision
            codigoDocumento   = s.codigoDocumento
            tipoIngreso       = s.tipoIngreso
            liquidar          = s.liquidar
            idTercero         = s.idTercero
            tipoIdentificacion = s.tipoIdentificacion
            email             = s.email.ifBlank { null }
            relacionado       = s.relacionado
            concepto          = ""
            subtotal          = s.subtotal
            totalDescuento    = s.totalDescuento
            total             = s.total
            totalImpuesto     = s.totalImpuesto
            numeroItems       = s.detalle.size
            formaPago         = FormaPago.CO
            diasCredito       = null
            valores              = valoresRequest
            detalle              = detalleRequest
            formasPagoSri        = formasPagoRequest
            informacionAdicional = s.camposAdicionales.map { InformacionAdicionalDto(it.nombre, it.valor) }.ifEmpty { null }
            ambiente          = s.ambiente
            cuentaPorCobrar   = s.cuentaPorCobrar
        }
    }

    // ── Cálculo de totales ────────────────────────────────────────────────────
    private fun recalcularTotales(detalle: List<DetalleItemUi>) {
        val subtotal  = detalle.fold(BigDecimal.ZERO) { a, d -> a.add(d.subtotalItem) }
        val totalDesc = detalle.fold(BigDecimal.ZERO) { a, d -> a.add(d.descuento) }

        var subtotal15           = BigDecimal.ZERO
        var subtotal5            = BigDecimal.ZERO
        var subtotalTarifaEspec  = BigDecimal.ZERO
        var subtotal0            = BigDecimal.ZERO
        var subtotalNoObjeto     = BigDecimal.ZERO
        var subtotalExento       = BigDecimal.ZERO
        var valorIce             = BigDecimal.ZERO
        var iva15                = BigDecimal.ZERO
        var iva5                 = BigDecimal.ZERO
        var ivaTarifaEspec       = BigDecimal.ZERO
        var totalImpuesto        = BigDecimal.ZERO

        detalle.forEach { d ->
            val imp  = d.impuesto ?: return@forEach
            val base = d.subtotalItem
            val tax  = base.multiply(imp.tarifa ?: BigDecimal.ZERO)
                .divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
            when (imp.codigo) {
                "2" -> when (imp.codigoPorcentaje) {
                    "4" -> { subtotal15          = subtotal15.add(base);         iva15          = iva15.add(tax) }
                    "5" -> { subtotal5           = subtotal5.add(base);          iva5           = iva5.add(tax) }
                    "0" -> subtotal0             = subtotal0.add(base)
                    "6" -> subtotalNoObjeto      = subtotalNoObjeto.add(base)
                    "7" -> subtotalExento        = subtotalExento.add(base)
                    "8" -> { subtotalTarifaEspec = subtotalTarifaEspec.add(base); ivaTarifaEspec = ivaTarifaEspec.add(tax) }
                }
                "3" -> valorIce = valorIce.add(tax)
            }
            totalImpuesto = totalImpuesto.add(tax)
        }

        val total = subtotal.add(totalImpuesto)
        _state.update { it.copy(
            subtotal             = subtotal,
            totalDescuento       = totalDesc,
            totalImpuesto        = totalImpuesto,
            total                = total,
            subtotal15           = subtotal15,
            subtotal5            = subtotal5,
            subtotalTarifaEspecial = subtotalTarifaEspec,
            subtotal0            = subtotal0,
            subtotalNoObjeto     = subtotalNoObjeto,
            subtotalExento       = subtotalExento,
            valorIce             = valorIce,
            iva15                = iva15,
            iva5                 = iva5,
            ivaTarifaEspecial    = ivaTarifaEspec
        ) }
    }

    private fun recalcularSubtotalFila(d: DetalleItemUi): DetalleItemUi {
        val sub = d.precioUnitario.multiply(d.cantidad).subtract(d.descuento).max(BigDecimal.ZERO)
        return d.copy(subtotalItem = sub)
    }

    // ── Tercero search — igual al campo Tercero del listado de facturas ──────────
    fun setNumeroIdentificacionBusqueda(v: String) {
        // Limpia selección previa al escribir
        _state.update { it.copy(
            numeroIdentificacion   = v,
            idTercero              = null,
            terceroDropdownVisible = false,
            terceroSugerencias     = emptyList()
        )}
        terceroSearchJob?.cancel()
        if (v.trim().length >= 2) {
            terceroSearchJob = scope.launch {
                delay(300)
                buscarTercerosPorId(v.trim())
            }
        }
    }

    private fun buscarTercerosPorId(query: String) {
        scope.launch {
            _state.update { it.copy(buscandoTercero = true) }
            try {
                val filterDto = GeTerceroFilterDto().apply { filter = query }
                val result    = tercerosService.findAllPaginate(idData, filterDto, PageRequest.of(0, 10, Sort.unsorted()))
                val lista     = result.content ?: emptyList()
                _state.update { it.copy(
                    buscandoTercero        = false,
                    terceroSugerencias     = lista,
                    terceroDropdownVisible = lista.isNotEmpty()
                )}
            } catch (e: Exception) {
                _state.update { it.copy(buscandoTercero = false, terceroSugerencias = emptyList(), terceroDropdownVisible = false) }
            }
        }
    }

    fun seleccionarTercero(t: GeTerceroGetListDto) {
        _state.update { it.copy(
            idTercero              = t.idTercero,
            terceroNombre          = t.tercero ?: "",
            terceroQuery           = t.tercero ?: "",
            tipoIdentificacion     = t.tipoIdentificacion?.let { ti -> runCatching { TipoIdentificacion.valueOf(ti) }.getOrNull() } ?: TipoIdentificacion.R,
            numeroIdentificacion   = t.numeroIdentificacion ?: "",
            email                  = t.email ?: "",
            direccion              = t.direccion ?: "",
            telefonos              = t.telefonos ?: "",
            terceroSugerencias     = emptyList(),
            terceroDropdownVisible = false
        )}
    }

    fun limpiarTercero() {
        _state.update { it.copy(
            idTercero              = null,
            terceroNombre          = "",
            terceroQuery           = "",
            tipoIdentificacion     = TipoIdentificacion.R,
            numeroIdentificacion   = "",
            email                  = "",
            direccion              = "",
            telefonos              = "",
            terceroSugerencias     = emptyList(),
            terceroDropdownVisible = false
        )}
        terceroSearchJob?.cancel()
    }

    fun cerrarDropdownTercero() = _state.update { it.copy(terceroDropdownVisible = false) }

    // ── Detalle — diálogo de búsqueda de item ────────────────────────────────
    fun abrirDialogoItem(key: UUID? = null) {
        _state.update { it.copy(
            showItemDialog       = true,
            itemDialogKeyDestino = key,
            itemDialogQuery      = "",
            itemDialogResultados = emptyList(),
            itemDialogBuscando   = false
        )}
    }

    fun cerrarDialogoItem() {
        itemDialogSearchJob?.cancel()
        _state.update { it.copy(showItemDialog = false, itemDialogKeyDestino = null, itemDialogQuery = "", itemDialogResultados = emptyList()) }
    }

    fun setItemDialogQuery(v: String) {
        _state.update { it.copy(itemDialogQuery = v, itemDialogResultados = emptyList()) }
        itemDialogSearchJob?.cancel()
        if (v.trim().length >= 2) {
            itemDialogSearchJob = scope.launch {
                delay(300)
                buscarItemsDialogo(v.trim())
            }
        }
    }

    private fun buscarItemsDialogo(query: String) {
        scope.launch {
            _state.update { it.copy(itemDialogBuscando = true) }
            try {
                val filterDto = GeItemListFilterDto().apply { filter = query }
                val result    = itemsService.findAllPaginate(idData, idEmpresa, filterDto, PageRequest.of(0, 30, Sort.unsorted()))
                _state.update { it.copy(itemDialogBuscando = false, itemDialogResultados = result.content ?: emptyList()) }
            } catch (e: Exception) {
                _state.update { it.copy(itemDialogBuscando = false, itemDialogResultados = emptyList()) }
            }
        }
    }

    fun seleccionarItemDesdeDialogo(item: GeItemGetListDto) {
        val s = _state.value
        // Primer impuesto del item; si no tiene, tomar el primero disponible
        val impUi = item.impuestos?.firstOrNull()?.let { itemImp ->
            s.impuestosDisponibles.find { it.codigo == itemImp.codigo && it.codigoPorcentaje == itemImp.codigoPorcentaje }
        } ?: s.impuestosDisponibles.firstOrNull()
        val precio = item.precios?.firstOrNull()?.precio1 ?: BigDecimal.ZERO
        val keyDestino = s.itemDialogKeyDestino

        val nuevaFila = DetalleItemUi(
            key             = keyDestino ?: UUID.randomUUID(),
            itemOrden       = if (keyDestino == null) s.detalle.size + 1 else s.detalle.find { it.key == keyDestino }?.itemOrden ?: 1,
            idItem          = item.idItem,
            codigoPrincipal = item.codigoPrincipal ?: "",
            codigoAuxiliar  = item.codigoAuxiliar ?: "",
            codigoBarras    = item.codigoBarras ?: "",
            descripcion     = item.descripcion ?: "",
            precioUnitario  = precio,
            cantidad        = s.detalle.find { it.key == keyDestino }?.cantidad ?: BigDecimal.ONE,
            descuento       = BigDecimal.ZERO,
            impuesto        = impUi
        ).let { recalcularSubtotalFila(it) }

        _state.update { st ->
            val nuevaLista = if (keyDestino == null) st.detalle + nuevaFila
                            else st.detalle.map { if (it.key == keyDestino) nuevaFila else it }
            st.copy(
                detalle              = nuevaLista,
                showItemDialog       = false,
                itemDialogKeyDestino = null,
                itemDialogQuery      = "",
                itemDialogResultados = emptyList()
            )
        }
        recalcularTotales(_state.value.detalle)
    }

    // ── Detalle — setters de campos ───────────────────────────────────────────
    fun setDetalleCantidad(key: UUID, v: BigDecimal) {
        _state.update { s ->
            val lista = s.detalle.map { d ->
                if (d.key != key) d else recalcularSubtotalFila(d.copy(cantidad = v))
            }
            s.copy(detalle = lista)
        }
        recalcularTotales(_state.value.detalle)
    }

    fun setDetallePrecio(key: UUID, v: BigDecimal) {
        _state.update { s ->
            val lista = s.detalle.map { d ->
                if (d.key != key) d else recalcularSubtotalFila(d.copy(precioUnitario = v))
            }
            s.copy(detalle = lista)
        }
        recalcularTotales(_state.value.detalle)
    }

    fun setDetalleDescuento(key: UUID, v: BigDecimal) {
        _state.update { s ->
            val lista = s.detalle.map { d ->
                if (d.key != key) d else recalcularSubtotalFila(d.copy(descuento = v))
            }
            s.copy(detalle = lista)
        }
        recalcularTotales(_state.value.detalle)
    }

    fun setDetalleImpuesto(key: UUID, imp: GeImpuestoResponseDto?) {
        _state.update { s ->
            s.copy(detalle = s.detalle.map { d -> if (d.key != key) d else d.copy(impuesto = imp) })
        }
        recalcularTotales(_state.value.detalle)
    }

    fun setDetalleCentroCostos(key: UUID, id: UUID?) {
        _state.update { s ->
            s.copy(detalle = s.detalle.map { d -> if (d.key != key) d else d.copy(idCentroCostos = id) })
        }
    }

    fun setDetalleUnidadMedida(key: UUID, v: String) {
        _state.update { s ->
            s.copy(detalle = s.detalle.map { d -> if (d.key != key) d else d.copy(unidadMedida = v) })
        }
    }

    // ── Detalle — agregar / eliminar ──────────────────────────────────────────
    fun agregarDetalle() = abrirDialogoItem()

    fun eliminarDetalle(key: UUID) {
        _state.update { s ->
            val lista = s.detalle.filter { it.key != key }
                .mapIndexed { idx, d -> d.copy(itemOrden = idx + 1) }
            s.copy(detalle = lista)
        }
        recalcularTotales(_state.value.detalle)
    }

    // ── Formas de pago SRI — diálogo ─────────────────────────────────────────
    fun abrirDialogoFormaPago(codigoPreseleccionado: String = "", descripcionPreseleccionada: String = "") {
        _state.update { it.copy(
            showFormaPagoDialog  = true,
            dialogFpCodigo       = codigoPreseleccionado,
            dialogFpDescripcion  = descripcionPreseleccionada,
            dialogFpValor        = if (it.formasPagoSri.isEmpty()) it.total else BigDecimal.ZERO,
            dialogFpPlazo        = "",
            dialogFpUnidadTiempo = "Días"
        )}
    }

    fun cerrarDialogoFormaPago() = _state.update { it.copy(showFormaPagoDialog = false) }

    fun setDialogFpSeleccion(codigo: String, descripcion: String) =
        _state.update { it.copy(dialogFpCodigo = codigo, dialogFpDescripcion = descripcion) }

    fun setDialogFpValor(v: BigDecimal)      = _state.update { it.copy(dialogFpValor = v) }
    fun setDialogFpPlazo(v: String)          = _state.update { it.copy(dialogFpPlazo = v) }
    fun setDialogFpUnidadTiempo(v: String)   = _state.update { it.copy(dialogFpUnidadTiempo = v) }

    fun confirmarFormaPago() {
        val s = _state.value
        if (s.dialogFpCodigo.isBlank()) {
            _state.update { it.copy(errorMessage = "Seleccione una forma de pago") }
            return
        }
        val nueva = FormaPagoSriUi(
            formaPago    = s.dialogFpCodigo,
            descripcion  = s.dialogFpDescripcion,
            total        = s.dialogFpValor,
            plazo        = s.dialogFpPlazo,
            unidadTiempo = s.dialogFpUnidadTiempo
        )
        _state.update { it.copy(formasPagoSri = it.formasPagoSri + nueva, showFormaPagoDialog = false) }
    }

    fun eliminarFormaPagoSri(key: UUID) =
        _state.update { it.copy(formasPagoSri = it.formasPagoSri.filter { fp -> fp.key != key }) }

    // ── Campos adicionales — diálogo ─────────────────────────────────────────
    fun abrirDialogoCampoAdicional() =
        _state.update { it.copy(showCampoAdicionalDialog = true, dialogCaNombre = "", dialogCaValor = "") }

    fun cerrarDialogoCampoAdicional() =
        _state.update { it.copy(showCampoAdicionalDialog = false, dialogCaNombre = "", dialogCaValor = "") }

    fun setDialogCaNombre(v: String) = _state.update { it.copy(dialogCaNombre = v) }
    fun setDialogCaValor(v: String)  = _state.update { it.copy(dialogCaValor = v) }

    fun confirmarCampoAdicional() {
        val s = _state.value
        if (s.dialogCaNombre.isBlank()) {
            _state.update { it.copy(errorMessage = "El nombre del campo no puede estar vacío") }
            return
        }
        val nuevo = CampoAdicionalUi(nombre = s.dialogCaNombre.trim(), valor = s.dialogCaValor.trim())
        _state.update { it.copy(
            camposAdicionales        = it.camposAdicionales + nuevo,
            showCampoAdicionalDialog = false,
            dialogCaNombre           = "",
            dialogCaValor            = ""
        )}
    }

    fun eliminarCampoAdicional(key: UUID) =
        _state.update { it.copy(camposAdicionales = it.camposAdicionales.filter { c -> c.key != key }) }

    fun togglePropina() = _state.update { it.copy(propina10 = !it.propina10) }

    // ── Setters de encabezado ─────────────────────────────────────────────────
    fun setSucursal(v: String)            = _state.update { it.copy(sucursal = v) }
    fun setFechaEmision(v: String)        = _state.update { it.copy(fechaEmision = v) }
    fun setSerie(v: String)               = _state.update { it.copy(serie = v) }
    fun setSecuencial(v: String)          = _state.update { it.copy(secuencial = v) }

    fun seleccionarSerie(serie: AdEmpresaSerieFacturaDto) {
        _state.update { it.copy(serie = serie.serie, secuencial = serie.secuencial ?: "") }
    }
    fun setTipoIngreso(v: TipoIngreso)    = _state.update { it.copy(tipoIngreso = v) }
    fun setCodigoDocumento(v: String)     = _state.update { it.copy(codigoDocumento = v) }
    fun setConcepto(v: String)            = _state.update { it.copy(concepto = v) }
    fun setAmbiente(v: Int)               = _state.update { it.copy(ambiente = v) }
    fun setCuentaPorCobrar(v: Boolean)    = _state.update { it.copy(cuentaPorCobrar = v) }
    fun setLiquidar(v: Liquidar)          = _state.update { it.copy(liquidar = v) }

    // ── Setters de cliente ────────────────────────────────────────────────────
    fun setTipoIdentificacion(v: TipoIdentificacion) = _state.update { it.copy(tipoIdentificacion = v) }
    fun setNumeroIdentificacion(v: String)            = _state.update { it.copy(numeroIdentificacion = v) } // edición directa sin búsqueda
    fun setTerceroNombre(v: String)                   = _state.update { it.copy(terceroNombre = v) }
    fun setEmail(v: String)                           = _state.update { it.copy(email = v) }
    fun setDireccion(v: String)                       = _state.update { it.copy(direccion = v) }
    fun setTelefonos(v: String)                       = _state.update { it.copy(telefonos = v) }
    fun setRelacionado(v: String)                     = _state.update { it.copy(relacionado = v) }

    // ── Setters de pago ───────────────────────────────────────────────────────
    fun setFormaPago(v: FormaPago) = _state.update { it.copy(formaPago = v) }
    fun setDiasCredito(v: Int)     = _state.update { it.copy(diasCredito = v) }

    fun dismissError()   = _state.update { it.copy(errorMessage = null) }
    fun onDestroy()      = scope.cancel()
}
