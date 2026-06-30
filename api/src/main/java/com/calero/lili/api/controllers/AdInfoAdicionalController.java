package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.adInfoAdicional.AdInfoAdicionalServiceImpl;
import com.calero.lili.core.adInfoAdicional.dto.AdInfoAdicionalRequestDto;
import com.calero.lili.core.adInfoAdicional.dto.AdInfoAdicionalResponseDto;
import com.calero.lili.core.dtos.PaginatedDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/info-adicional")
@CrossOrigin(originPatterns = "*")
public class AdInfoAdicionalController {

    private final AdInfoAdicionalServiceImpl adInfoAdicionalService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;


    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    public AdInfoAdicionalResponseDto create(@RequestBody @Valid AdInfoAdicionalRequestDto request,
                                             @PathVariable("idEmpresa") Long idEmpresa) {
        return adInfoAdicionalService.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idInfoAdicional}")
    @ResponseStatus(HttpStatus.OK)
    public AdInfoAdicionalResponseDto update(@PathVariable("idInfoAdicional") UUID idInfoAdicional,
                                             @PathVariable("idEmpresa") Long idEmpresa,
                                             @RequestBody @Valid AdInfoAdicionalRequestDto request) {

        return adInfoAdicionalService.update(idDataService.getIdData(), idEmpresa, idInfoAdicional,
                request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{idInfoAdicional}")
    @ResponseStatus(HttpStatus.OK)
    public AdInfoAdicionalResponseDto findById(@PathVariable("idInfoAdicional") UUID idInfoAdicional,
                                               @PathVariable("idEmpresa") Long idEmpresa) {
        return adInfoAdicionalService.findById(idDataService.getIdData(), idEmpresa, idInfoAdicional);
    }

    @GetMapping("listar/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<AdInfoAdicionalResponseDto> findAll(@PathVariable("idEmpresa") Long idEmpresa,
                                                            Pageable pageable) {
        return adInfoAdicionalService.findAll(idDataService.getIdData(), idEmpresa, pageable);
    }


    @DeleteMapping("{idEmpresa}/{idInfoAdicional}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idInfoAdicional") UUID idInfoAdicional,
                       @PathVariable("idEmpresa") Long idEmpresa) {
        adInfoAdicionalService.delete(idDataService.getIdData(), idEmpresa, idInfoAdicional, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }


}
