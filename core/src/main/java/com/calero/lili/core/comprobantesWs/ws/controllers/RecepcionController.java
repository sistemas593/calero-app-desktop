package com.calero.lili.core.comprobantesWs.ws.controllers;

import com.calero.lili.core.comprobantesWs.ws.dtos.recepcion.RecepcionRequestDto;
import com.calero.lili.core.comprobantesWs.ws.services.RecepcionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import recepcion.ws.sri.gob.ec.RespuestaSolicitud;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/ws-recepcion")
@CrossOrigin(originPatterns = "*")

public class RecepcionController {

    private final RecepcionServiceImpl recepcionService;
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public RespuestaSolicitud create(
            @RequestBody RecepcionRequestDto request) {
        return recepcionService.enviar(request);
    }

}
