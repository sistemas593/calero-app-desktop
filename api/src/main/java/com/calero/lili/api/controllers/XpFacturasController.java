package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.modCxP.XpFacturas.XpFacturaServiceImpl;
import com.calero.lili.core.modCxP.XpFacturas.dto.FilterXpFacturaDto;
import com.calero.lili.core.modCxP.XpFacturas.dto.XpFacturasRequestDto;
import com.calero.lili.core.modCxP.XpFacturas.dto.XpFacturasResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping(value = "api/v1.0/cxp-facturas")
@RequiredArgsConstructor
public class XpFacturasController {

    private final XpFacturaServiceImpl xpFacturaService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PX_FC_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody XpFacturasRequestDto request) {
        return xpFacturaService.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idFactura}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('PX_FC_MO_PR','PX_FC_MO_SC','PX_FC_MO_TD')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idFactura") UUID idFactura,
                              @Valid @RequestBody XpFacturasRequestDto request,
                              FilterXpFacturaDto filters) {
        return xpFacturaService.update(idDataService.getIdData(), idEmpresa, idFactura, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoModificarXpFactura());
    }

    @DeleteMapping("{idEmpresa}/delete/{idFactura}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('PX_FC_EL_PR','PX_FC_EL_SC','PX_FC_EL_TD')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idFactura") UUID idFactura,
                       FilterXpFacturaDto filters) {
        xpFacturaService.delete(idDataService.getIdData(), idEmpresa, idFactura,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoEliminarXpFactura());
    }

    @GetMapping("{idEmpresa}/findById/{idFactura}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('PX_FC_VR_PR','PX_FC_VR_SC','PX_FC_VR_TD')")
    public XpFacturasResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                          @PathVariable("idFactura") UUID idFactura,
                                          FilterXpFacturaDto filters) {
        return xpFacturaService.findById(idDataService.getIdData(), idEmpresa, idFactura,
                filters,
                auditorAware.getTipoPermisoVerXpFactura(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('PX_FC_VR_PR','PX_FC_VR_SC','PX_FC_VR_TD')")
    public PaginatedDto<XpFacturasResponseDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                               FilterXpFacturaDto filters,
                                                               Pageable pageable) {
        return xpFacturaService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }
}
