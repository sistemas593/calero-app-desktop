package com.calero.lili.api.controllers;

import com.calero.lili.core.modAdminlistaNegra.EmailProcesarRechazados;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/procesar-rechazados")
@RequiredArgsConstructor
public class EmailProcesarRechazadosController {

    private final EmailProcesarRechazados procesarRechazados;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void create() {
        procesarRechazados.procesarRechazados();
    }


}
