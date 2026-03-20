package com.calero.lili.api.controllers;

import com.calero.lili.core.apiSitac.dtos.stFechaActualizacion.AdStDatosRequestDto;
import com.calero.lili.core.apiSitac.dtos.stFechaActualizacion.AdStFechaActualizacionGetDto;
import com.calero.lili.core.apiSitac.services.StFechaActualizacionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("apist/v1.0/datos")
@CrossOrigin(originPatterns = "*")

public class AdStDatosController {

    private final StFechaActualizacionServiceImpl adDatasService;

    // SITAC LEE ESTE ENDPOINT PARA ver los datos del usuario clave y fecha de actualizacion del sistema

    @GetMapping("clave/{clave}")
    @ResponseStatus(HttpStatus.OK)
    public AdStFechaActualizacionGetDto findByClave(@PathVariable("clave") String clave) {
        return adDatasService.findByClave(clave);
    }

    // LA PAGINA WEB LEE ESTE ENDPOINT PARA MOSTRAR LA FECHA DE ACTUALIZACION DEL SISTEMA
    @GetMapping("fecha-actualizacion")
    @ResponseStatus(HttpStatus.OK)
    public AdStFechaActualizacionGetDto fechaActualizacion(
    ) {
        return adDatasService.findFechaActualizacion();
    }

    // LA PAGINA WEB LEE ESTE ENDPOINT PARA DESCARGAR LAS ACTUALIZACIONES POR RUC O VERSION GRATUITA
    // SE RECIBE LOS DATOS QUE EL USUARIO INGRESA EN EL FORMULARIO
    @PostMapping("ruc/{ruc}")
    @ResponseStatus(HttpStatus.OK)
    public AdStFechaActualizacionGetDto findByRuc(@RequestBody AdStDatosRequestDto request,
                                                  @PathVariable("ruc") String ruc) {
        return adDatasService.findByRuc(ruc, request);
    }


    @GetMapping("fecha-actualizacion-sitacfacturador")
    @ResponseStatus(HttpStatus.OK)
    public AdStFechaActualizacionGetDto fechaActualizacionSitacfacturador(
    ) {
        return adDatasService.findFechaActualizacionSitacfacturador();
    }


}
