package com.calero.lili.api.modImpuestosProcesos;

import com.calero.lili.api.dtos.FilterFechasDto;
import com.calero.lili.api.modImpuestosProcesos.dto.ImpuestoProcesoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/impuesto-procesos")
@CrossOrigin(originPatterns = "*")
public class ImpuestoProcesosController {

    private final ImpuestoProcesosServicesImpl impuestoProcesosServices;

    @PostMapping("/cruce-facturas-retenciones")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ImpuestoProcesoResponseDto> validarReferencia(FilterFechasDto filtro) {
        return impuestoProcesosServices.validarReferencia(filtro);
    }

}
