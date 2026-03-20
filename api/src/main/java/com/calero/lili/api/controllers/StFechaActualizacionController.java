package com.calero.lili.api.controllers;

import com.calero.lili.core.apiSitac.dtos.stFechaActualizacion.AdStFechaActualizacionCreationRequestDto;
import com.calero.lili.core.apiSitac.dtos.stFechaActualizacion.AdStFechaActualizacionGetDto;
import com.calero.lili.core.apiSitac.services.StFechaActualizacionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/fecha-actualizacion")
@CrossOrigin(originPatterns = "*")

public class StFechaActualizacionController {

    private final StFechaActualizacionServiceImpl adDatasService;

    //Update la fecha de actualizacion del sistema
    @PutMapping("{idSistema}")
    @ResponseStatus(HttpStatus.OK)
    public AdStFechaActualizacionGetDto update(@PathVariable("idSistema") String idSistema,
                                               @RequestBody AdStFechaActualizacionCreationRequestDto request) {
        return adDatasService.update(idSistema, request);
    }

}
