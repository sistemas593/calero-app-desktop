package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirCreationRequestDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestoCargaExcelService;
import com.calero.lili.core.modCompras.modComprasImpuestos.CpImpuestosServiceImpl;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.CreationCompraImpuestoRequestDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.FilterListCompraImpuestoDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.GetDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.GetListDto;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.GetListDtoTotalizado;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.GetReporteListDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/impuestos")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class CpImpuestosController {

    private final CpImpuestosServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;
    private final CpImpuestoCargaExcelService cpImpuestoCargaExcelService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CP_CI_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody CreationCompraImpuestoRequestDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idCompraImpuesto}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CP_CI_MO_PR','CP_CI_MO_SC','CP_CI_MO_TD')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idCompraImpuesto") UUID idCompraImpuesto,
                              @RequestBody CreationCompraImpuestoRequestDto request,
                              FilterListCompraImpuestoDto filters) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idCompraImpuesto, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoModificarImpuesto());
    }


    @PutMapping("facturas-retencion/{idEmpresa}/{idCompraImpuesto}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CP_CI_MO_PR','CP_CI_MO_SC','CP_CI_MO_TD')")
    public ResponseDto updateFacturaDesdeRetencion(@PathVariable("idEmpresa") Long idEmpresa,
                                                   @PathVariable("idCompraImpuesto") UUID idCompraImpuesto,
                                                   @RequestBody CreationCompraImpuestoRequestDto request,
                                                   FilterListCompraImpuestoDto filters) {
        return vtVentasService.updateFacturaDesdeRetencion(idDataService.getIdData(), idEmpresa, idCompraImpuesto, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoModificarImpuesto());
    }

    @DeleteMapping("facturas/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('CP_CI_EL_PR','CP_CI_EL_SC','CP_CI_EL_TD')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idRecibida") UUID idRecibida,
                       FilterListCompraImpuestoDto filters) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idRecibida,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoEliminarImpuesto());
    }

    @GetMapping("facturas/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CP_CI_VR_PR','CP_CI_VR_SC','CP_CI_VR_TD')")
    public GetDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                           @PathVariable("idRecibida") UUID idRecibida,
                           FilterListCompraImpuestoDto filters) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idRecibida,
                filters,
                auditorAware.getTipoPermisoVerImpuesto(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }


    // TODO HACER EL PAGINADO EN TODOS LOS PAGINADOS
    @GetMapping("facturas/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CP_CI_VR_PR','CP_CI_VR_SC','CP_CI_VR_TD')")
    public PaginatedDto<GetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                    FilterListCompraImpuestoDto filters,
                                                    Pageable pageable) {

        Sort sort = pageable.getSort().and(Sort.by("idImpuestos").ascending());

        int pageSize = pageable.getPageSize();
        if (pageSize > 100) {
            pageSize = 100;}
        Pageable pageableConSort = PageRequest.of(pageable.getPageNumber(), pageSize, sort);

        return vtVentasService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageableConSort,
                auditorAware.getTipoPermisoVerImpuesto(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }


    @GetMapping("reporte/facturas/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CP_CI_VR_PR','CP_CI_VR_SC','CP_CI_VR_TD')")
    public GetListDtoTotalizado<GetReporteListDto> findAllPaginateTotalizado(@PathVariable("idEmpresa") Long idEmpresa,
                                                                             FilterListCompraImpuestoDto filters,
                                                                             Pageable pageable) {

        Sort sort = pageable.getSort().and(Sort.by("idImpuestos").ascending());

        int pageSize = pageable.getPageSize();
        if (pageSize > 100) {
            pageSize = 100;}
        Pageable pageableConSort = PageRequest.of(pageable.getPageNumber(), pageSize, sort);

        return vtVentasService.findAllPaginateTotalizado(idDataService.getIdData(), idEmpresa, filters, pageableConSort,
                auditorAware.getTipoPermisoVerImpuesto(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }


    @GetMapping("facturas/excel/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_CI_EX')")
    public void exportarExcel(@PathVariable("idEmpresa") Long idEmpresa,
                              FilterListCompraImpuestoDto filter,
                              HttpServletResponse response) throws IOException {
        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        vtVentasService.exportarExcel(idDataService.getIdData(), idEmpresa, response, filter);
    }

    @GetMapping("facturas/pdf/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_CI_EX')")
    public void exportarPDF(@PathVariable("idEmpresa") Long idEmpresa,
                            FilterListCompraImpuestoDto filter,
                            HttpServletResponse response) throws IOException {
        vtVentasService.exportarPDF(idDataService.getIdData(), idEmpresa, response, filter);
    }

    @PutMapping("facturas/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CP_CI_MO_PR','CP_CI_MO_SC','CP_CI_MO_TD')")
    public void updateDatos(@PathVariable("idEmpresa") Long idEmpresa,
                            @PathVariable("idRecibida") UUID idRecibida,
                            @RequestBody CpImpuestosRecibirCreationRequestDto request) {
        vtVentasService.updateDatos(idDataService.getIdData(), idEmpresa, idRecibida, request);
    }


    @PostMapping("/excel/{idEmpresa}")
    public void uploadCpImpuestoExcel(@RequestParam("file") MultipartFile file,
                                      @PathVariable("idEmpresa") Long idEmpresa,
                                      @PathVariable("sucursal") String sucursal) {
        try {
            cpImpuestoCargaExcelService.cargarExcelCompraImpuestos(idDataService.getIdData(), idEmpresa,
                    file, auditorAware.getCurrentAuditor().orElse("SYSTEM"), sucursal);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
