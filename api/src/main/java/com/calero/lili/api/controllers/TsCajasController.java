package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.TsCajasServiceImpl;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.dto.TsCajasRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.dto.TsCajasResponseDto;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1.0/cajas")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class TsCajasController {

    private final TsCajasServiceImpl service;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public TsCajasResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                     @Valid @RequestBody TsCajasRequestDto request) {
        return service.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }


    @PutMapping("{idEmpresa}/{idCaja}")
    @ResponseStatus(code = HttpStatus.OK)
    public TsCajasResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                     @PathVariable("idCaja") UUID idCaja,
                                     @Valid @RequestBody TsCajasRequestDto request) {
        return service.update(idDataService.getIdData(), idEmpresa, idCaja,
                request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }


    @DeleteMapping("{idEmpresa}/{idCaja}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idCaja") UUID idCaja) {
        service.delete(idDataService.getIdData(), idEmpresa, idCaja,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{idCaja}")
    @ResponseStatus(HttpStatus.OK)
    public TsCajasResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                       @PathVariable("idCaja") UUID idCaja) {
        return service.findById(idDataService.getIdData(), idEmpresa, idCaja);
    }


    @GetMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedDto<TsCajasResponseDto> findAll(@PathVariable("idEmpresa") Long idEmpresa,
                                                    Pageable pageable) {
        return service.findAll(idDataService.getIdData(), idEmpresa, pageable);
    }

}
