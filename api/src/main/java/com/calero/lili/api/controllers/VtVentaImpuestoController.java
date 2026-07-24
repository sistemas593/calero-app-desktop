package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.modVentas.dto.GetVentasListDto;
import com.calero.lili.core.modVentas.facturas.dto.FilterListVentasDto;
import com.calero.lili.core.modVentas.modVentasImpuestos.VtVentasImpuestoService;
import com.calero.lili.core.modVentas.modVentasImpuestos.dto.CreationVentaImpuestoRequestDto;
import com.calero.lili.core.modVentas.modVentasImpuestos.dto.VentaImpuestoResponseDto;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/ventas-impuestos")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class VtVentaImpuestoController {

    private final VtVentasImpuestoService vtVentasImpuestoService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_FC_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody CreationVentaImpuestoRequestDto request) {
        return vtVentasImpuestoService.create(idDataService.getIdData(), idEmpresa,
                request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_FC_MO_PR', 'VT_FC_MO_SC', 'VT_FC_MO_TD')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idVenta") UUID idVenta,
                              @RequestBody CreationVentaImpuestoRequestDto request,
                              FilterListVentasDto filters) {
        return vtVentasImpuestoService.update(idDataService.getIdData(), idEmpresa, idVenta, request, filters,
                auditorAware.getTipoPermisoFacturaModificar(), auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('VT_FC_EL_PR', 'VT_FC_EL_SC', 'VT_FC_EL_TD')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idVenta") UUID idVenta,
                       FilterListVentasDto filters) {
        vtVentasImpuestoService.delete(idDataService.getIdData(), idEmpresa, idVenta, filters,
                auditorAware.getTipoPermisoFacturaEliminar(), auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_FC_VR_PR', 'VT_FC_VR_SC', 'VT_FC_VR_TD')")
    public VentaImpuestoResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                             @PathVariable("idVenta") UUID idVenta,
                                             FilterListVentasDto filters) {
        return vtVentasImpuestoService.findById(idDataService.getIdData(), idEmpresa, idVenta, filters,
                auditorAware.getTipoPermisoFacturaVer(), auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_FC_VR_PR', 'VT_FC_VR_SC', 'VT_FC_VR_TD')")
    public PaginatedDto<GetVentasListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                          FilterListVentasDto filters,
                                                          Pageable pageable) {

        Sort sort = pageable.getSort().and(Sort.by("idVenta").ascending());

        int pageSize = pageable.getPageSize();
        if (pageSize > 100) {
            pageSize = 100;}
        Pageable pageableConSort = PageRequest.of(pageable.getPageNumber(), pageSize, sort);

        return vtVentasImpuestoService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageableConSort,
                auditorAware.getTipoPermisoFacturaVer(), auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PostMapping("anulada/{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_FC_AN_PR', 'VT_FC_AN_SC', 'VT_FC_AN_TD')")
    public ResponseDto updateAnulada(@PathVariable("idEmpresa") Long idEmpresa,
                                     @PathVariable("idVenta") UUID idVenta,
                                     FilterListVentasDto filters) {
        return vtVentasImpuestoService.updateAnulada(idDataService.getIdData(), idEmpresa, idVenta, filters,
                auditorAware.getTipoPermisoFacturaAnular(), auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }
}
