package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.comprobantesWs.RespuestaProcesoGetDto;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.modVentas.dto.GetListDto;
import com.calero.lili.core.modVentas.dto.GetListDtoTotalizado;
import com.calero.lili.core.modVentas.notasDebito.VtVentasNotasDebitoServiceImpl;
import com.calero.lili.core.modVentas.notasDebito.dto.CreationNotaDebitoRequestDto;
import com.calero.lili.core.modVentas.notasDebito.dto.FilterListDto;
import com.calero.lili.core.modVentas.notasDebito.dto.GetNotaDebitoDto;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/ventas")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class VtVentasNotasDebitoController {

    private final VtVentasNotasDebitoServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("notas-debito/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_ND_CR')")
    public RespuestaProcesoGetDto createNotaDebito(@PathVariable("idEmpresa") Long idEmpresa,
                                                   @Valid @RequestBody CreationNotaDebitoRequestDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"), "WEB");
    }

    @PutMapping("notas-debito/{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_ND_MO_PR','VT_ND_MO_SC','VT_ND_MO_TD')")
    public ResponseDto updateNotaDebito(@PathVariable("idEmpresa") Long idEmpresa,
                                        @PathVariable("idVenta") UUID idVenta,
                                        @RequestBody CreationNotaDebitoRequestDto request,
                                        FilterListDto filters) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idVenta, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoModificarNotaDebito());
    }

    @DeleteMapping("notas-debito/{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('VT_ND_EL_PR','VT_ND_EL_SC','VT_ND_EL_TD')")
    public void deleteNotaDebito(@PathVariable("idEmpresa") Long idEmpresa,
                                 @PathVariable("idVenta") UUID idVenta,
                                 FilterListDto filters) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idVenta,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoEliminarNotaDebito());
    }

    @GetMapping("notas-debito/{idEmpresa}/{idVenta}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_ND_VR_PR','VT_ND_VR_SC','VT_ND_VR_TD')")
    public GetNotaDebitoDto findNotaDebitoById(@PathVariable("idEmpresa") Long idEmpresa,
                                               @PathVariable("idVenta") UUID idVenta,
                                               FilterListDto filters) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idVenta,
                filters,
                auditorAware.getTipoPermisoVerNotaDebito(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("notas-debito/mensajes/{idEmpresa}/{idVenta}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_ND_VR_PR','VT_ND_VR_SC','VT_ND_VR_TD')")
    public List<Mensajes> findByIdMensajes(@PathVariable("idEmpresa") Long idEmpresa,
                                           @PathVariable("idVenta") UUID idVenta,
                                           FilterListDto filters) {
        return vtVentasService.findByIdMensajes(idDataService.getIdData(), idEmpresa, idVenta,
                filters,
                auditorAware.getTipoPermisoVerNotaDebito(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PostMapping("notas-debito/anulada/{idEmpresa}/{idVenta}")
    @PreAuthorize("hasAnyAuthority('VT_ND_AN_PR','VT_ND_AN_SC','VT_ND_AN_TD')")
    public ResponseDto updateAnulada(@PathVariable("idEmpresa") Long idEmpresa,
                                     @PathVariable("idVenta") UUID idVenta,
                                     FilterListDto filters) {
        return vtVentasService.updateAnulada(idDataService.getIdData(), idEmpresa, idVenta,
                filters,
                auditorAware.getTipoPermisoAnularNotaDebito(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("notas-debito/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_ND_VR_PR','VT_ND_VR_SC','VT_ND_VR_TD')")
    public PaginatedDto<GetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                    FilterListDto filters, Pageable pageable) {
        return vtVentasService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable,
                auditorAware.getTipoPermisoVerNotaDebito(), auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }
}
