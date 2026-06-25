package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias.TsComprobanteSecuenciaServiceImpl;
import com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias.dto.TsComprobanteSecuenciaRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias.dto.TsComprobanteSecuenciaResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
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
@RequestMapping(value = "api/v1.0/tesoreria/comprobantes-secuencias")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class TsComprobantesSecuenciasController {

    private final TsComprobanteSecuenciaServiceImpl service;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public TsComprobanteSecuenciaResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                                    @Valid @RequestBody TsComprobanteSecuenciaRequestDto request) {
        return service.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idComprobanteSecuencia}")
    @ResponseStatus(code = HttpStatus.OK)
    public TsComprobanteSecuenciaResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                                    @PathVariable("idComprobanteSecuencia") UUID idComprobanteSecuencia,
                                                    @Valid @RequestBody TsComprobanteSecuenciaRequestDto request) {
        return service.update(idDataService.getIdData(), idEmpresa, idComprobanteSecuencia,
                request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{idComprobanteSecuencia}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idComprobanteSecuencia") UUID idComprobanteSecuencia) {
        service.delete(idDataService.getIdData(), idEmpresa, idComprobanteSecuencia,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{idComprobanteSecuencia}")
    @ResponseStatus(HttpStatus.OK)
    public TsComprobanteSecuenciaResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                                      @PathVariable("idComprobanteSecuencia") UUID idComprobanteSecuencia) {
        return service.findById(idDataService.getIdData(), idEmpresa, idComprobanteSecuencia);
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    public List<TsComprobanteSecuenciaResponseDto> findAll(@PathVariable("idEmpresa") Long idEmpresa) {
        return service.findAll(idDataService.getIdData(), idEmpresa);
    }

}
