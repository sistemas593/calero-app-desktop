package com.calero.lili.api.controllers;

import com.calero.lili.core.adConfiguracion.AdMailEnviadosServiceImpl;
import com.calero.lili.core.adConfiguracion.AdMailEnviadosTotalServiceImpl;
import com.calero.lili.core.adConfiguracion.dto.AdMailEnviadosResponseDto;
import com.calero.lili.core.adConfiguracion.dto.AdMailEnviadosTotalResponseDto;
import com.calero.lili.core.adConfiguracion.dto.FilterMailEnviadosDto;
import com.calero.lili.core.adConfiguracion.dto.FilterMailEnviadosTotalesDto;
import com.calero.lili.core.dtos.PaginatedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/correos-enviados")
@CrossOrigin(originPatterns = "*")
public class AdMailEnviadosController {


    private final AdMailEnviadosServiceImpl adMailEnviadosService;
    private final AdMailEnviadosTotalServiceImpl adMailEnviadosTotalService;


    /*@GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedDto<AdMailEnviadosResponseDto> findAllEnviados(FilterMailEnviadosDto model,
                                                                   Pageable pageable) {
        return adMailEnviadosService.findAllPaginate(model, pageable);
    }

    @GetMapping("totales")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedDto<AdMailEnviadosTotalResponseDto> findAllTotales(FilterMailEnviadosTotalesDto model,
                                                                       Pageable pageable) {
        return adMailEnviadosTotalService.findAllPaginate(model, pageable);
    }*/

}
