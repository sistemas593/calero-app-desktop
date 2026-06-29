package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.modContabilidad.modSecuenciales.CnSecuenciasServiceImpl;
import com.calero.lili.core.modContabilidad.modSecuenciales.dto.CnSecuenciasRequestDto;
import com.calero.lili.core.modContabilidad.modSecuenciales.dto.CnSecuenciasResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
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
@RequestMapping(value = "api/v1.0/contabilidad/secuencias")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class CnSecuenciasController {

    private final CnSecuenciasServiceImpl service;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CnSecuenciasResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                          @Valid @RequestBody CnSecuenciasRequestDto request) {
        return service.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idSecuencia}")
    @ResponseStatus(code = HttpStatus.OK)
    public CnSecuenciasResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                          @PathVariable("idSecuencia") UUID idSecuencia,
                                          @Valid @RequestBody CnSecuenciasRequestDto request) {
        return service.update(idDataService.getIdData(), idEmpresa, idSecuencia,
                request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{idSecuencia}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idSecuencia") UUID idSecuencia) {
        service.delete(idDataService.getIdData(), idEmpresa, idSecuencia,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{idSecuencia}")
    @ResponseStatus(HttpStatus.OK)
    public CnSecuenciasResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                            @PathVariable("idSecuencia") UUID idSecuencia) {
        return service.findById(idDataService.getIdData(), idEmpresa, idSecuencia);
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedDto<CnSecuenciasResponseDto> findAll(@PathVariable("idEmpresa") Long idEmpresa,
                                                         Pageable pageable) {
        return service.findAllPaginate(idDataService.getIdData(), idEmpresa, pageable);

    }

}
