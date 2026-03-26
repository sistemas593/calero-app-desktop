package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.modAdminDatas.AdDatasServiceImpl;
import com.calero.lili.core.modAdminDatas.dto.AdDataResponseConfiguracionDto;
import com.calero.lili.core.modAdminDatas.dto.AdDatasCreationRequestDto;
import com.calero.lili.core.modAdminDatas.dto.AdDatasDto;
import com.calero.lili.core.modClientesConfiguraciones.dto.VtClientesConfiguracionesGetOneDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("api/v1.0/datas")
@CrossOrigin(originPatterns = "*")

public class AdDatasController {

    private final AdDatasServiceImpl adDatasService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CF_DT_CR')")
    public AdDataResponseConfiguracionDto create(@RequestBody AdDatasCreationRequestDto request) {
        return adDatasService.create(request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idData}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_DT_MO')")
    public AdDataResponseConfiguracionDto update(@PathVariable("idData") Long idData,
                                                 @RequestBody AdDatasCreationRequestDto request) {
        return adDatasService.update(idData, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }


    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_DT_VR')")
    public PaginatedDto<AdDatasDto> findAllPaginate(FilterDto filters,
                                                    Pageable pageable) {
        return adDatasService.findAllPaginate(filters, pageable);
    }


    @GetMapping("{idData}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_DT_VR')")
    public AdDatasDto findById(@PathVariable("idData") Long idData) {
        return adDatasService.findByIdData(idData);
    }


    @GetMapping("cliente-configuracion/{idData}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_DT_VR')")
    public VtClientesConfiguracionesGetOneDto findByIdDataConfiguracion(@PathVariable("idData") Long idData) {
        return adDatasService.findByIdDataConfiguracion(idData);
    }

}
