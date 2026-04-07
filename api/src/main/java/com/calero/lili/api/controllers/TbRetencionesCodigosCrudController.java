package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.tablas.tbRetencionesCodigos.TbRetencionesCodigosCreationRequestDto;
import com.calero.lili.core.tablas.tbRetencionesCodigos.TbRetencionesCodigosGetListDto;
import com.calero.lili.core.tablas.tbRetencionesCodigos.TbRetencionesCodigosGetOneDto;
import com.calero.lili.core.tablas.tbRetencionesCodigos.TbRetencionesCodigosServiceImpl;
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
@RequestMapping("api/v1.0/listas-crud/retenciones-codigos")
@CrossOrigin(originPatterns = "*")

public class TbRetencionesCodigosCrudController {

    private final TbRetencionesCodigosServiceImpl tbService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public ResponseDto create(@RequestBody TbRetencionesCodigosCreationRequestDto request) {
        return tbService.create(auditorAware.getCurrentAuditor().orElse("SYSTEM"), request);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public ResponseDto update(@PathVariable("id") Long id,
                              @RequestBody TbRetencionesCodigosCreationRequestDto request) {
        return tbService.update(auditorAware.getCurrentAuditor().orElse("SYSTEM"), id, request);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public void delete(@PathVariable("id") Long id) {
        tbService.delete(auditorAware.getCurrentAuditor().orElse("SYSTEM"), id);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public TbRetencionesCodigosGetOneDto findById(@PathVariable("id") Long id) {
        return tbService.findById(id);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<TbRetencionesCodigosGetListDto> findAllPaginate(FilterDto filters,
                                                                        Pageable pageable) {
        return tbService.findAllPaginate(filters, pageable);
    }

}
