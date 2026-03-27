package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.comprobantesWs.dto.FilterEmailDto;
import com.calero.lili.core.comprobantesWs.services.ProcesarReenvioCorreoServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/procesar-documentos/reenviar-email")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class ReenviarEmailController {

    @Autowired
    private ProcesarReenvioCorreoServiceImpl procesarReenvioCorreoService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("ventas/{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void reenviarEmailVentas(@PathVariable("idEmpresa") Long idEmpresa,
                                    @PathVariable("id") UUID id,
                                    FilterEmailDto filter) {
        procesarReenvioCorreoService.procesarVentasReenvioCorreo(idDataService.getIdData(), idEmpresa, id, filter);
    }

    @PostMapping("guias-remision/{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void procesarGuiasRemisionReenvioCorreo(@PathVariable("idEmpresa") Long idEmpresa,
                                                   @PathVariable("id") UUID id,
                                                   FilterEmailDto filter) {
        procesarReenvioCorreoService.procesarGuiasRemisionReenvioCorreo(idDataService.getIdData(), idEmpresa, id, filter);
    }

    @PostMapping("liquidaciones/{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void procesarLiquidacionesReenvioCorreo(@PathVariable("idEmpresa") Long idEmpresa,
                                                   @PathVariable("id") UUID id,
                                                   FilterEmailDto filter) {
        procesarReenvioCorreoService.procesarLiquidacionesReenvioCorreo(idDataService.getIdData(), idEmpresa, id, filter);
    }

    @PostMapping("comprobantes-retencion/{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void procesarComprobantesRetencionReenvioCorreo(@PathVariable("idEmpresa") Long idEmpresa,
                                                           @PathVariable("id") UUID id,
                                                           FilterEmailDto filter) {
        procesarReenvioCorreoService.procesarComprobantesRetencionReenvioCorreo(idDataService.getIdData(), idEmpresa, id, filter);
    }

}
