package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.modVentas.dto.GetListDto;
import com.calero.lili.core.modVentas.dto.GetListDtoTotalizado;
import com.calero.lili.core.modVentas.facturas.VtVentasFacturasExcelService;
import com.calero.lili.core.modVentas.facturas.VtVentasFacturasServiceImpl;
import com.calero.lili.core.modVentas.facturas.dto.CreationFacturaRequestDto;
import com.calero.lili.core.modVentas.facturas.dto.FilterListDto;
import com.calero.lili.core.modVentas.facturas.dto.GetFacturaDto;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/ventas")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class VtVentasFacturasController {

    private final VtVentasFacturasServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;
    private final VtVentasFacturasExcelService vtVentasFacturasExcelService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("facturas/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_FC_CR')")
    public ResponseDto create(
            @PathVariable("idEmpresa") Long idEmpresa,
            @Valid @RequestBody CreationFacturaRequestDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("facturas/{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_FC_MO_PR, VT_FC_MO_SC, VT_FC_MO_TD')")
    public ResponseDto updateFactura(@PathVariable("idEmpresa") Long idEmpresa,
                                     @PathVariable("idVenta") UUID idVenta,
                                     @RequestBody CreationFacturaRequestDto request,
                                     FilterListDto filters) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idVenta, request, filters,
                auditorAware.getTipoPermisoFacturaModificar(), auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("facturas/{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('VT_FC_EL_PR, VT_FC_EL_SC, VT_FC_EL_TD')")
    public void deleteFactura(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idVenta") UUID idVenta,
                              FilterListDto filters) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idVenta, filters,
                auditorAware.getTipoPermisoFacturaEliminar(), auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("facturas/{idEmpresa}/{idVenta}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_FC_VR_PR','VT_FC_VR_SC','VT_FC_VR_TD')")
    public GetFacturaDto findFacturaById(@PathVariable("idEmpresa") Long idEmpresa,
                                         @PathVariable("idVenta") UUID idVenta,
                                         FilterListDto filters) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idVenta, filters,
                auditorAware.getTipoPermisoFacturaVer(), auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }


    @GetMapping("facturas/mensajes/{idEmpresa}/{idVenta}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_FC_VR_PR','VT_FC_VR_SC','VT_FC_VR_TD')")
    public List<Mensajes> findMensajeById(@PathVariable("idEmpresa") Long idEmpresa,
                                          @PathVariable("idVenta") UUID idVenta,
                                          FilterListDto filters) {

        return vtVentasService.findMensajeById(idDataService.getIdData(), idEmpresa, idVenta, filters,
                auditorAware.getTipoPermisoFacturaVer(), auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("facturas/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_FC_VR_PR','VT_FC_VR_SC','VT_FC_VR_TD')")
    public PaginatedDto<GetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                    FilterListDto filters,
                                                    Pageable pageable) {
        return vtVentasService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable,
                auditorAware.getTipoPermisoFacturaVer(), auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("facturas/reportes/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_FC_VR_PR','VT_FC_VR_SC','VT_FC_VR_TD')")
    public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(@PathVariable("idEmpresa") Long idEmpresa,
                                                                      FilterListDto filters,
                                                                      Pageable pageable) {
        log.info("Filters = {}", filters);
        return vtVentasService.findAllPaginateTotalizado(idDataService.getIdData(), idEmpresa, filters,
                auditorAware.getTipoPermisoFacturaVer(), auditorAware.getCurrentAuditor().orElse("SYSTEM") ,pageable);
    }

  /*  @GetMapping("excel/{idEmpresa}")
    @PreAuthorize("hasAuthority('VT_FC_EX')")
    public void exportarExcel(HttpServletResponse response,
                              @PathVariable("idEmpresa") Long idEmpresa,
                              FilterListDto filter) throws IOException {
        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        vtVentasService.exportarExcel(idDataService.getIdData(), idEmpresa, response, filter);
    }

    @GetMapping("pdf/{idEmpresa}")
    @PreAuthorize("hasAuthority('VT_FC_EX')")
    public void exportarPDF(HttpServletResponse response,
                            @PathVariable("idEmpresa") Long idEmpresa,
                            FilterListDto filters) throws DocumentException, IOException {
        vtVentasService.exportarPDF(idDataService.getIdData(), idEmpresa, response, filters);
    }*/

    @PostMapping("facturas/anulada/{idEmpresa}/{idVenta}")
    @PreAuthorize("hasAuthority('VT_FC_AN_PR ,VT_FC_AN_SC, VT_FC_AN_TD')")
    public ResponseDto updateAnulada(@PathVariable("idEmpresa") Long idEmpresa,
                                     @PathVariable("idVenta") UUID idVenta,
                                     FilterListDto filters) {
        return vtVentasService.updateAnulada(idDataService.getIdData(), idEmpresa, idVenta, filters,
                auditorAware.getTipoPermisoFacturaAnular(), auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PostMapping("facturas/excel/{idEmpresa}/{sucursal}")
    @PreAuthorize("hasAuthority('VT_FC_IMEX')")
    public void uploadFacturasExcel(@RequestParam("file") MultipartFile file,
                                    @PathVariable("idEmpresa") Long idEmpresa) {
        try {
            vtVentasFacturasExcelService.cargarExcelFacturas(idDataService.getIdData(), idEmpresa, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("facturas/asiento/{idEmpresa}/{idVenta}")
    @PreAuthorize("hasAuthority('VT_FC_CR')")
    public ResponseDto createAsientoVenta(@PathVariable("idEmpresa") Long idEmpresa,
                                          @PathVariable("idVenta") UUID idVenta) {
        return vtVentasService.createAsientoVenta(idDataService.getIdData(), idEmpresa, idVenta);
    }

}
