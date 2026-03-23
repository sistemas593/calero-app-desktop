package com.calero.lili.api.controllers;

import com.calero.lili.core.varios.recibirCorreoImap.StRecibidasServicesImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/procesar-recibidas-correo-electronico")
@CrossOrigin(originPatterns = "*")

public class StRecibidasCorreoController {

    private final StRecibidasServicesImpl stRecibidasServices;

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public void update() {
        stRecibidasServices.procesar();
    }
}
