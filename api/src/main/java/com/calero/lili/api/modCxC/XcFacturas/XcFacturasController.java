package com.calero.lili.api.modCxC.XcFacturas;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.modCxC.XcFacturas.dto.FilterXcFacturaDto;
import com.calero.lili.api.modCxC.XcFacturas.dto.RequestXcFacturasDto;
import com.calero.lili.api.modCxC.XcFacturas.dto.ResponseXcFacturasDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
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
@RequestMapping(value = "api/v1.0/cxc-facturas")
@RequiredArgsConstructor
public class XcFacturasController {

    private final XcFacturaServiceImpl tsComprobanteIngresoService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CX_FC_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody RequestXcFacturasDto request) {
        return tsComprobanteIngresoService.create(idDataService.getIdData(), idEmpresa, request,
                UUID.randomUUID(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idFactura}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CX_FC_MO_PR','CX_FC_MO_SC','CX_FC_MO_TD')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idFactura") UUID idFactura,
                              @Valid @RequestBody RequestXcFacturasDto request,
                              FilterXcFacturaDto filters) {
        return tsComprobanteIngresoService.update(idDataService.getIdData(), idEmpresa, idFactura, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoModificarXcFactura());
    }

    @DeleteMapping("{idEmpresa}/delete/{idFactura}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('CX_FC_EL_PR','CX_FC_EL_SC','CX_FC_EL_TD')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idFactura") UUID idFactura,
                       FilterXcFacturaDto filters) {
        tsComprobanteIngresoService.delete(idDataService.getIdData(), idEmpresa, idFactura,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoEliminarXcFactura());
    }

    @GetMapping("{idEmpresa}/findById/{idFactura}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CX_FC_VR_PR','CX_FC_VR_SC','CX_FC_VR_TD')")
    public ResponseXcFacturasDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                          @PathVariable("idFactura") UUID idFactura,
                                          FilterXcFacturaDto filters) {
        return tsComprobanteIngresoService.findById(idDataService.getIdData(), idEmpresa, idFactura,
                filters,
                auditorAware.getTipoPermisoVerXcFactura(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CX_FC_VR_PR','CX_FC_VR_SC','CX_FC_VR_TD')")
    public PaginatedDto<ResponseXcFacturasDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                               FilterXcFacturaDto filters,
                                                               Pageable pageable) {
        return tsComprobanteIngresoService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }
}
