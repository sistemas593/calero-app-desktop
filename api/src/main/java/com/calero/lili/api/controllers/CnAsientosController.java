package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosServiceImpl;
import com.calero.lili.core.modContabilidad.modAsientos.dto.CreationAsientosRequestDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.FilterListDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.GetDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.GetListDto;
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

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/contabilidad")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class CnAsientosController {

    private final CnAsientosServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CN_AS_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody CreationAsientosRequestDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idAsiento}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CN_AS_MO_PR','CN_AS_MO_SC','CN_AS_MO_TD')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idAsiento") UUID idAsiento,
                              @RequestBody CreationAsientosRequestDto request,
                              FilterListDto filters) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idAsiento, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoModificarAsiento());
    }

    @DeleteMapping("{idEmpresa}/{idAsiento}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('CN_AS_EL_PR','CN_AS_EL_SC','CN_AS_EL_TD')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idAsiento") UUID idAsiento,
                       FilterListDto filters) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idAsiento,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoEliminarAsiento());
    }

    @GetMapping("{idEmpresa}/{idAsiento}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CN_AS_VR_PR','CN_AS_VR_SC','CN_AS_VR_TD')")
    public GetDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                           @PathVariable("idAsiento") UUID idAsiento,
                           FilterListDto filters) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idAsiento,
                filters,
                auditorAware.getTipoPermisoVerAsiento(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CN_AS_VR_PR','CN_AS_VR_SC','CN_AS_VR_TD')")
    public PaginatedDto<GetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                    FilterListDto filters,
                                                    Pageable pageable) {
        return vtVentasService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable,
                auditorAware.getTipoPermisoVerAsiento(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }
}
