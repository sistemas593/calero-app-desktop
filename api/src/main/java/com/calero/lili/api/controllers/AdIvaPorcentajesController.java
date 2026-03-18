package com.calero.lili.api.controllers;

import com.calero.lili.core.modAdminPorcentajes.AdIvaPorcentajeServiceImpl;
import com.calero.lili.core.modAdminPorcentajes.dto.AdIvaPorcentajesDto;
import com.calero.lili.core.modAdminPorcentajes.dto.AdIvaPorcentajesResponseDto;
import com.calero.lili.core.modAdminPorcentajes.dto.FilterListDto;
import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/iva-porcentajes")
@CrossOrigin(originPatterns = "*")
public class AdIvaPorcentajesController {

    private final AdIvaPorcentajeServiceImpl adIvaPorcentajeService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('US_PE_CR')")
    public AdIvaPorcentajesResponseDto create(@RequestBody @Valid AdIvaPorcentajesDto request) {
        return adIvaPorcentajeService.create(request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idPermiso}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_PE_MO')")
    public AdIvaPorcentajesResponseDto update(@PathVariable("idPermiso") Long idPermiso,
                                              @RequestBody @Valid AdIvaPorcentajesDto request) {

        return adIvaPorcentajeService.update(idPermiso, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idPermiso}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_PE_VR')")
    public AdIvaPorcentajesResponseDto findById(@PathVariable("idPermiso") Long idPermiso) {
        return adIvaPorcentajeService.findById(idPermiso);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_PE_VR')")
    public PaginatedDto<AdIvaPorcentajesResponseDto> findAllPaginate(FilterListDto filters,
                                                                     Pageable pageable) {
        return adIvaPorcentajeService.findAllPaginate(filters, pageable);
    }


    @DeleteMapping("{idPermiso}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('US_PE_EL')")
    public void delete(@PathVariable("idPermiso") Long idPermiso) {
        adIvaPorcentajeService.delete(idPermiso, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

}
