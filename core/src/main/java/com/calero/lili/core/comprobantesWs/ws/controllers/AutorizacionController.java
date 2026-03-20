package com.calero.lili.core.comprobantesWs.ws.controllers;

import autorizacion.ws.sri.gob.ec.RespuestaComprobante;
import com.calero.lili.core.comprobantesWs.ws.dtos.autorizacion.AutorizacionRequestDto;
import com.calero.lili.core.comprobantesWs.ws.services.AutorizacionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/ws-consulta")
@CrossOrigin(originPatterns = "*")

public class AutorizacionController {

    private final AutorizacionServiceImpl consultaClaveAccesoService;
    @GetMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public RespuestaComprobante create(
            @RequestBody AutorizacionRequestDto request) {
        return consultaClaveAccesoService.consulta(request);
    }

}
