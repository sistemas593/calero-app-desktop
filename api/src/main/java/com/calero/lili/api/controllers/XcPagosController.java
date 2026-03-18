package com.calero.lili.api.controllers;


import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.modCxC.XcFacturas.dto.FilterXcFacturaDto;
import com.calero.lili.core.modCxC.XcPagos.XcPagoServiceImpl;
import com.calero.lili.core.modCxC.XcPagos.dto.RequestPagoDto;
import com.calero.lili.core.modCxC.XcPagos.dto.ResponseXcPagosDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/cxc-pagos")
@RequiredArgsConstructor
public class XcPagosController {

    private final XcPagoServiceImpl xcPagoService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CX_PG_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody RequestPagoDto request) {
        return xcPagoService.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idGrupoFactura}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CX_PG_MO_PR','CX_PG_MO_SC','CX_PG_MO_TD')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idGrupoFactura") UUID idGrupoFactura,
                              @Valid @RequestBody RequestPagoDto request,
                              FilterXcFacturaDto filters) {
        return xcPagoService.update(idDataService.getIdData(), idGrupoFactura, idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoModificarXcPago());
    }

    @DeleteMapping("{idEmpresa}/{idGrupoFactura}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('CX_PG_EL_PR','CX_PG_EL_SC','CX_PG_EL_TD')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idGrupoFactura") UUID idGrupoFactura,
                       FilterXcFacturaDto filters) {
        xcPagoService.delete(idDataService.getIdData(), idEmpresa, idGrupoFactura,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoEliminarXcPago());
    }

    @GetMapping("{idEmpresa}/{idGrupoFactura}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CX_PG_VR_PR','CX_PG_VR_SC','CX_PG_VR_TD')")
    public List<ResponseXcPagosDto> findById(@PathVariable("idEmpresa") Long idEmpresa,
                                             @PathVariable("idGrupoFactura") UUID idGrupoFactura,
                                             FilterXcFacturaDto filters) {
        return xcPagoService.getFindByIdFactura(idDataService.getIdData(), idEmpresa, idGrupoFactura,
                filters,
                auditorAware.getTipoPermisoVerXcPago(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

}
