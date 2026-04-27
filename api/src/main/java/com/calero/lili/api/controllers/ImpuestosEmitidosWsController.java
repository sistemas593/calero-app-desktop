package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.comprobantesWs.services.DeEmitidasWsServiceImpl;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListCreationRequestDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListExistRequestResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/emitidos")
@CrossOrigin(originPatterns = "*")

public class ImpuestosEmitidosWsController {

    private final DeEmitidasWsServiceImpl deEmitidasService;
    private final IdDataServiceImpl idDataService;
    private final DeEmitidasWsServiceImpl deEmitidasWsService;
    private final AuditorAwareImpl auditorAware;

    // Verificar si existen
    @PostMapping("verificar/{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    public CpImpuestosRecibirListExistRequestResponseDto verificarClaveAccesoExiste(
            @PathVariable("idEmpresa") Long idEmpresa,
            @RequestBody CpImpuestosRecibirListExistRequestResponseDto request) {
        return deEmitidasWsService.verificarExisteListaClaves(idDataService.getIdData(), idEmpresa, request);

    }

    @PostMapping("list/{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    public CpImpuestosRecibirListCreationResponseDto createListClavesAcceso(@PathVariable("idEmpresa") Long idEmpresa,
                                                                            @RequestBody CpImpuestosRecibirListCreationRequestDto request) {
        return deEmitidasService.createListClavesAcceso(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));

    }

}
