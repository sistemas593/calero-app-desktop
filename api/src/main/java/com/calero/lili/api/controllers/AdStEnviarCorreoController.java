package com.calero.lili.api.controllers;

import com.calero.lili.core.apiSitac.services.StEnviarCorreoSitacServiceImpl;
import com.calero.lili.core.modAdDatasConfiguraciones.dto.StCorreoRequestDto;
import com.calero.lili.core.modAdDatasConfiguraciones.dto.StEmpresasEnviarCorreoResponseDto;
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
@RequestMapping("apist/v1.0/send")
@CrossOrigin(originPatterns = "*")

public class AdStEnviarCorreoController {

    private final StEnviarCorreoSitacServiceImpl stEnviarCorreoSitacService;

    @PostMapping("{clave}")
    @ResponseStatus(HttpStatus.OK)
    public StEmpresasEnviarCorreoResponseDto enviarCorreo(@PathVariable("clave") String clave,
                                                          @RequestBody StCorreoRequestDto request) {
        return stEnviarCorreoSitacService.enviarCorreoSitac(clave, request);
    }

}
