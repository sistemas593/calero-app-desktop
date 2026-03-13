package com.calero.lili.api.modVentasPedidos;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modVentasPedidos.dto.CreationComprasPedidosRequestDto;
import com.calero.lili.api.modVentasPedidos.dto.FilterListDto;
import com.calero.lili.api.modVentasPedidos.dto.GetDto;
import com.calero.lili.api.modVentasPedidos.dto.GetListDto;
import com.calero.lili.api.modVentasPedidos.dto.GetListDtoTotalizado;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/pedidos")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class PedidosController {

    private final PedidosServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_PD_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody CreationComprasPedidosRequestDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_PD_MO')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idVenta") UUID idVenta,
                              @RequestBody CreationComprasPedidosRequestDto request) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idVenta, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('VT_PD_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idVenta") UUID idVenta) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idVenta, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{idVenta}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_PD_VR')")
    public GetDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                           @PathVariable("idVenta") UUID idVenta) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idVenta);
    }

//    @GetMapping("{idEmpresa}/{idVenta}")
//    @ResponseStatus(code = HttpStatus.OK)
//    public VtVentaGetDto findByIdProjection(
//            @PathVariable("idEmpresa") Long idEmpresa,
//            @PathVariable("idVenta") UUID idVenta) {
//        return vtVentasService.findByIdProjection(idEmpresa, idVenta);
//    }

//    @GetMapping("facturas/{idEmpresa}")
//    @ResponseStatus(code = HttpStatus.OK)
//    public PaginatedDto<VtVentaGetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
//                                                           VtVentaFilterListDto filters,
//                                                           Pageable pageable) {
//        return vtVentasService.findAllPaginate(idEmpresa, filters, pageable);
//    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_PD_VR')")
    public PaginatedDto<GetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                    FilterListDto filters,
                                                    Pageable pageable) {
        return vtVentasService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

    @GetMapping("reportes/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_PD_VR')")
    public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(
            @PathVariable("idEmpresa") Long idEmpresa,
            FilterListDto filters,
            Pageable pageable) {
        log.info("Filters = {}", filters);
        return vtVentasService.findAllPaginateTotalizado(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

    @GetMapping("excel/{idEmpresa}")
    @PreAuthorize("hasAuthority('VT_PD_EX')")
    public void exportarExcel(HttpServletResponse response,
                              @PathVariable("idEmpresa") Long idEmpresa,
                              FilterListDto filter) throws IOException {
        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        vtVentasService.exportarExcel(idDataService.getIdData(), idEmpresa, response, filter);
    }

    @GetMapping("pdf/{idEmpresa}")
    @PreAuthorize("hasAuthority('VT_PD_EX')")
    public void exportarPDF(HttpServletResponse response,
                            @PathVariable("idEmpresa") Long idEmpresa,
                            FilterListDto filters) throws DocumentException, IOException {
        vtVentasService.exportarPDF(idDataService.getIdData(), idEmpresa, response, filters);
    }


    //////////////////////////////////

//    /**
//     * Busca facturas según diferentes criterios.
//     *
//     * @param idData Identificador de la data.
//     * @param idEmpresa Identificador de la empresa.
//     * @param idTercero Identificador del cliente (opcional).
//     * @param numeroIdentificacion Número de identificación (opcional).
//     * @param fechaEmisionDesde Fecha de inicio para el rango de emisión de facturas.
//     * @param fechaEmisionHasta Fecha de fin para el rango de emisión de facturas (opcional).
//     * @param serie Serie de la factura (opcional).
//     * @param secuencia Secuencia de la factura (opcional).
//     * @return Respuesta HTTP que incluye un mapa con las facturas encontradas o un mensaje de error.
//     */
//
//    // FECHA ENVIAR FORMATO 01/01/2000
//    @GetMapping("/buscar")
//    public ResponseEntity<Map<String, Object>> buscarFacturas(
//            @RequestParam(required = true) Long idData,
//            @RequestParam(required = true) Long idEmpresa,
//            @RequestParam(required = false) Long idTercero,
//            @RequestParam(required = false) String numeroIdentificacion,
//            @RequestParam(name = "fechaEmisionDesde", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fechaEmisionDesde,
//            @RequestParam(name = "fechaEmisionHasta", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fechaEmisionHasta,
//            @RequestParam(required = false) String serie,
//            @RequestParam(required = false) String secuencia,
//            Pageable pageable
//    ) {
//
//        if ((fechaEmisionDesde == null || fechaEmisionHasta == null) && (serie == null || secuencia == null)) {
//            return new ResponseEntity<>(Collections.singletonMap("error", "Debe especificar el rango de fechas de emisión o serie y secuencia."), HttpStatus.BAD_REQUEST);
//        }
//
//        VtVentasFilterSpecification filter = new VtVentasFilterSpecification.Builder()
//                .idData(idData)
//                .idEmpresa(idEmpresa)
//                .idTercero(idTercero)
//                .numeroIdentificacion(numeroIdentificacion)
//                .fechaEmisionDesde(fechaEmisionDesde)
//                .fechaEmisionHasta(fechaEmisionHasta)
//                .serie(serie)
//                .secuencia(secuencia)
//                .build();
//
//        Optional<List<VtGuiaEntity>> optionalFacturas = vtVentasService.findFacturasWithSpecification(filter, PageRequest.of(0, Integer.MAX_VALUE));
//
//        if (optionalFacturas.isPresent()) {
//            List<VtGuiaEntity> facturas = optionalFacturas.get();
//            if (facturas.isEmpty()) {
//                return new ResponseEntity<>(Collections.singletonMap("error", "No se encontraron facturas con los filtros proporcionados"), HttpStatus.NOT_FOUND);
//            }
//            return new ResponseEntity<>(Collections.singletonMap("facturas", facturas), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(Collections.singletonMap("error", "Ocurrió un error al buscar las facturas"), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//
//    /**
//     * Obtiene los detalles de las facturas según los parámetros especificados.
//     *
//     * @param idData Identificador de la data. Es obligatorio para la búsqueda.
//     * @param idEmpresa Identificador de la empresa. Es obligatorio para la búsqueda.
//     * @param idTercero Identificador del cliente. Es un parámetro opcional.
//     * @param numeroIdentificacion Número de identificación del cliente. Es un parámetro opcional.
//     * @param fechaEmisionDesde Fecha desde la que se quieren obtener facturas. Es obligatorio.
//     * @param fechaEmisionHasta Fecha hasta la que se quieren obtener facturas. Es un parámetro opcional.
//     * @param serie Serie de la factura. Es un parámetro opcional.
//     * @param secuencia Secuencia de la factura. Es un parámetro opcional.
//     * @param idItem Identificador del ítem en la factura. Es un parámetro opcional.
//     * @return Respuesta con los detalles de las facturas o un mensaje de error.
//     */
//    @GetMapping("/detalles")
//    public ResponseEntity<Map<String, Object>> obtenerDetallesFactura(
//            @RequestParam(required = true) Long idData,
//            @RequestParam(required = true) Long idEmpresa,
//            @RequestParam(required = false) Long idTercero,
//            @RequestParam(required = false) String numeroIdentificacion,
//            @RequestParam(name = "fechaEmisionDesde", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fechaEmisionDesde,
//            @RequestParam(name = "fechaEmisionHasta", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fechaEmisionHasta,
//            @RequestParam(required = false) String serie,
//            @RequestParam(required = false) String secuencia,
//            @RequestParam(required = false) Long idItem,
//            Pageable pageable
//    ) {
//
//
//        if ((fechaEmisionDesde == null || fechaEmisionHasta == null) && (serie == null || secuencia == null)) {
//            return new ResponseEntity<>(Collections.singletonMap("error", "Debe especificar el rango de fechas de emisión o serie y secuencia."), HttpStatus.BAD_REQUEST);
//        }
//
//        VtVentasFilterSpecification filter = new VtVentasFilterSpecification.Builder()
//                .idData(idData)
//                .idEmpresa(idEmpresa)
//                .idTercero(idTercero)
//                .numeroIdentificacion(numeroIdentificacion)
//                .fechaEmisionDesde(fechaEmisionDesde)
//                .fechaEmisionHasta(fechaEmisionHasta)
//                .serie(serie)
//                .secuencia(secuencia)
//                .idItem(idItem) // Parámetro adicional
//                .build();
//
//        Optional<List<VtGuiaDetalleEntity>> facturas = vtVentasService.findFacturaDetailsWithSpecification(filter);
//        Optional<List<VtGuiaEntity>> FacturasDetalle = vtVentasService.findFacturasWithSpecification(filter, pageable);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("facturas", facturas);
//        response.put("facturaDetalles", FacturasDetalle);
//
//        if (facturas.isPresent() && FacturasDetalle.isPresent()) {
//            List<VtGuiaDetalleEntity> detalles = facturas.get();
//            if (detalles.isEmpty()) {
//                return new ResponseEntity<>(Collections.singletonMap("error", "No se encontraron los detalles de las facturas con los filtros proporcionados"), HttpStatus.NOT_FOUND);
//            }
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(Collections.singletonMap("error", "Ocurrió un error al buscar el detalle de las facturas"), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    /**
//     * Obtiene los totales de las facturas según los parámetros especificados.
//     *
//     * @param idData Identificador de la data. Es obligatorio para la búsqueda.
//     * @param idEmpresa Identificador de la empresa. Es obligatorio para la búsqueda.
//     * @param idTercero Identificador del cliente. Es un parámetro opcional.
//     * @param numeroIdentificacion Número de identificación del cliente. Es un parámetro opcional.
//     * @param fechaEmisionDesde Fecha desde la que se quieren obtener facturas. Es obligatorio.
//     * @param fechaEmisionHasta Fecha hasta la que se quieren obtener facturas. Es un parámetro opcional.
//     * @param serie Serie de la factura. Es un parámetro opcional.
//     * @param secuencia Secuencia de la factura. Es un parámetro opcional.
//     * @return Respuesta con los totales de las facturas o un mensaje de error.
//     */
//    @GetMapping("/totales")
//    public ResponseEntity<Map<String, Object>> obtenerTotalesFacturas(
//            @RequestParam(required = true) Long idData,
//            @RequestParam(required = true) Long idEmpresa,
//            @RequestParam(required = false) Long idTercero,
//            @RequestParam(required = false) String numeroIdentificacion,
//            @RequestParam(name = "fechaEmisionDesde", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fechaEmisionDesde,
//            @RequestParam(name = "fechaEmisionHasta", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fechaEmisionHasta,
//            @RequestParam(required = false) String serie,
//            @RequestParam(required = false) String secuencia,
//            Pageable pageable
//    ) {
//
//        if ((fechaEmisionDesde == null || fechaEmisionHasta == null) && (serie == null || secuencia == null)) {
//            return new ResponseEntity<>(Collections.singletonMap("error", "Debe especificar el rango de fechas de emisión o serie y secuencia."), HttpStatus.BAD_REQUEST);
//        }
//
//
//        VtVentasFilterSpecification filter = new VtVentasFilterSpecification.Builder()
//                .idData(idData)
//                .idEmpresa(idEmpresa)
//                .idTercero(idTercero)
//                .numeroIdentificacion(numeroIdentificacion)
//                .fechaEmisionDesde(fechaEmisionDesde)
//                .fechaEmisionHasta(fechaEmisionHasta)
//                .serie(serie)
//                .secuencia(secuencia)
//                .build();
//
//        Optional<Map<String, BigDecimal>> totales = vtVentasService.calculateInvoiceTotalsWithSpecification(filter);
//        Optional<List<VtGuiaEntity>> Facturas = vtVentasService.findFacturasWithSpecification(filter, pageable);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("facturas", Facturas);
//        response.put("totales", totales);
//
//        if (totales.isPresent() && Facturas.isPresent()) {
//            List<VtGuiaEntity> facturas = Facturas.get();
//            if (facturas.isEmpty()) {
//                return new ResponseEntity<>(Collections.singletonMap("error", "No se encontraron facturas con los filtros proporcionados"), HttpStatus.NOT_FOUND);
//            }
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(Collections.singletonMap("error", "Ocurrió un error al buscar los totales de las facturas"), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//    }
//
//    /**
//     * Obtiene los totales detallados de las facturas según los parámetros especificados.
//     *
//     * @param idData Identificador de la data. Es obligatorio para la búsqueda.
//     * @param idEmpresa Identificador de la empresa. Es obligatorio para la búsqueda.
//     * @param idTercero Identificador del cliente. Es un parámetro opcional.
//     * @param numeroIdentificacion Número de identificación del cliente. Es un parámetro opcional.
//     * @param fechaEmisionDesde Fecha desde la que se quieren obtener facturas. Es obligatorio.
//     * @param fechaEmisionHasta Fecha hasta la que se quieren obtener facturas. Es un parámetro opcional.
//     * @param serie Serie de la factura. Es un parámetro opcional.
//     * @param secuencia Secuencia de la factura. Es un parámetro opcional.
//     * @param idItem Identificador del ítem en la factura. Es un parámetro opcional.
//     * @return Respuesta con los totales detallados de las facturas o un mensaje de error.
//     */
//    @GetMapping("/totales-detalles")
//    public ResponseEntity<Map<String, Object>> obtenerTotalesFacturasDetalles(
//            @RequestParam Long idData,
//            @RequestParam Long idEmpresa,
//            @RequestParam(required = false) Long idTercero,
//            @RequestParam(required = false) String numeroIdentificacion,
//            @RequestParam(name = "fechaEmisionDesde", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fechaEmisionDesde,
//            @RequestParam(name = "fechaEmisionHasta", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fechaEmisionHasta,
//            @RequestParam(required = false) String serie,
//            @RequestParam(required = false) String secuencia,
//            @RequestParam(required = false) Long idItem,
//            Pageable pageable
//    ) {
//
//        if ((fechaEmisionDesde == null || fechaEmisionHasta == null) && (serie == null || secuencia == null)) {
//            return new ResponseEntity<>(Collections.singletonMap("error", "Debe especificar el rango de fechas de emisión o serie y secuencia."), HttpStatus.BAD_REQUEST);
//        }
//
//
//        VtVentasFilterSpecification filter = new VtVentasFilterSpecification.Builder()
//                .idData(idData)
//                .idEmpresa(idEmpresa)
//                .idTercero(idTercero)
//                .numeroIdentificacion(numeroIdentificacion)
//                .fechaEmisionDesde(fechaEmisionDesde)
//                .fechaEmisionHasta(fechaEmisionHasta)
//                .serie(serie)
//                .secuencia(secuencia)
//                .idItem(idItem)
//                .build();
//
//        Optional<Map<String, BigDecimal>> totalesDetalles = vtVentasService.calculateInvoiceDetailsTotalsWithSpecification(filter);
//        Optional<List<VtGuiaEntity>> Facturas = vtVentasService.findFacturasWithSpecification(filter, pageable);
//        Optional<List<VtGuiaDetalleEntity>> FacturasDetalle = vtVentasService.findFacturaDetailsWithSpecification(filter);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("facturas", Facturas);
//        response.put("facturaDetalles", FacturasDetalle);
//        response.put("totalesDetalles", totalesDetalles);
//
//        if (totalesDetalles.isPresent() && Facturas.isPresent() && FacturasDetalle.isPresent()) {
//            List<VtGuiaDetalleEntity> detalles = FacturasDetalle.get();
//            if (detalles.isEmpty()) {
//                return new ResponseEntity<>(Collections.singletonMap("error", "No se encontraron los detalles de las facturas con los filtros proporcionados"), HttpStatus.NOT_FOUND);
//            }
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(Collections.singletonMap("error", "Ocurrió un error al buscar los totales de los detalles de las facturas"), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//    }

}
