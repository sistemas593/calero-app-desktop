package com.calero.lili.api.modCxP.XpPagos;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.modCxP.XpFacturas.dto.FilterXpFacturaDto;
import com.calero.lili.api.modCxP.XpPagos.dto.RequestPagoXpDto;
import com.calero.lili.api.modCxP.XpPagos.dto.ResponsePagoXpDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.ResponseDto;
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
@RequestMapping(value = "api/v1.0/cxp-pagos")
@RequiredArgsConstructor
public class XpPagosController {

    private final XpPagoServiceImpl xpPagoService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PX_PG_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody RequestPagoXpDto request) {
        return xpPagoService.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idGrupoFactura}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('PX_PG_MO_PR','PX_PG_MO_SC','PX_PG_MO_TD')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idGrupoFactura") UUID idGrupoFactura,
                              @Valid @RequestBody RequestPagoXpDto request,
                              FilterXpFacturaDto filters) {
        return xpPagoService.update(idDataService.getIdData(), idGrupoFactura, idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoModificarXpPago());
    }

    @DeleteMapping("{idEmpresa}/{idGrupoFactura}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('PX_PG_EL_PR','PX_PG_EL_SC','PX_PG_EL_TD')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idGrupoFactura") UUID idGrupoFactura,
                       FilterXpFacturaDto filters) {
        xpPagoService.delete(idDataService.getIdData(), idEmpresa, idGrupoFactura,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoEliminarXpPago());
    }

    @GetMapping("{idEmpresa}/{idGrupoFactura}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('PX_PG_VR_PR','PX_PG_VR_SC','PX_PG_VR_TD')")
    public List<ResponsePagoXpDto> findById(@PathVariable("idEmpresa") Long idEmpresa,
                                             @PathVariable("idGrupoFactura") UUID idGrupoFactura,
                                             FilterXpFacturaDto filters) {
        return xpPagoService.getFindByIdFactura(idDataService.getIdData(), idEmpresa, idGrupoFactura,
                filters,
                auditorAware.getTipoPermisoVerXpPago(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

}
