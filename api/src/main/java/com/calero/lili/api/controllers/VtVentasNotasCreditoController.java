package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.comprobantesWs.RespuestaProcesoGetDto;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.modVentas.facturas.dto.FilterListDto;
import com.calero.lili.core.modVentas.notasCredito.VtVentasNotasCreditoServiceImpl;
import com.calero.lili.core.modVentas.notasCredito.dto.CreationNotaCreditoRequestDto;
import com.calero.lili.core.modVentas.notasCredito.dto.GetNotaCreditoDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/ventas")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class VtVentasNotasCreditoController {

    private final VtVentasNotasCreditoServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("notas-credito/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_NC_CR')")
    public RespuestaProcesoGetDto createNotaCredito(@PathVariable("idEmpresa") Long idEmpresa,
                                                    @Valid @RequestBody CreationNotaCreditoRequestDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"), "WEB");
    }

    @PutMapping("notas-credito/{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_NC_MO_PR','VT_NC_MO_SC','VT_NC_MO_TD')")
    public ResponseDto updateNotaCredito(@PathVariable("idEmpresa") Long idEmpresa,
                                         @PathVariable("idVenta") UUID idVenta,
                                         @RequestBody CreationNotaCreditoRequestDto request,
                                         FilterListDto filters) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idVenta, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                auditorAware.getTipoPermisoModificarNotaCredito(),
                filters);
    }

    @DeleteMapping("notas-credito/{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('VT_NC_EL_PR','VT_NC_EL_SC','VT_NC_EL_TD')")
    public void deleteNotaCredito(@PathVariable("idEmpresa") Long idEmpresa,
                                  @PathVariable("idVenta") UUID idVenta,
                                  FilterListDto filters) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idVenta,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                auditorAware.getTipoPermisoEliminarNotaCredito(),
                filters);
    }

    @GetMapping("notas-credito/{idEmpresa}/{idVenta}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_NC_VR_PR','VT_NC_VR_SC','VT_NC_VR_TD')")
    public GetNotaCreditoDto findNotaCreditoById(@PathVariable("idEmpresa") Long idEmpresa,
                                                 @PathVariable("idVenta") UUID idVenta,
                                                 FilterListDto filters) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idVenta,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                auditorAware.getTipoPermisoVerNotaCredito(),
                filters);
    }

    @GetMapping("notas-credito/mensajes/{idEmpresa}/{idVenta}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_NC_VR_PR','VT_NC_VR_SC','VT_NC_VR_TD')")
    public List<Mensajes> findByMensajesForId(@PathVariable("idEmpresa") Long idEmpresa,
                                              @PathVariable("idVenta") UUID idVenta,
                                              FilterListDto filters) {
        return vtVentasService.findByMensajes(idDataService.getIdData(), idEmpresa, idVenta,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                auditorAware.getTipoPermisoVerNotaCredito(),
                filters);
    }

    @PostMapping("notas-credito/anulada/{idEmpresa}/{idVenta}")
    @PreAuthorize("hasAnyAuthority('VT_NC_AN_PR','VT_NC_AN_SC','VT_NC_AN_TD')")
    public ResponseDto updateAnulada(@PathVariable("idEmpresa") Long idEmpresa,
                                     @PathVariable("idVenta") UUID idVenta,
                                     FilterListDto filters) {
        return vtVentasService.updateAnulada(idDataService.getIdData(), idEmpresa, idVenta,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                auditorAware.getTipoPermisoAnularNotaCredito(),
                filters);
    }

    @PostMapping("notas-credito/asiento/{idEmpresa}/{idVenta}")
    @PreAuthorize("hasAuthority('VT_NC_CR')")
    public ResponseDto createAsientoVenta(@PathVariable("idEmpresa") Long idEmpresa,
                                          @PathVariable("idVenta") UUID idVenta) {
        return vtVentasService.createAsientoNotaCredito(idDataService.getIdData(), idEmpresa, idVenta);
    }

}
