package com.calero.lili.api.controllers;

import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpComprasXMLLiquidacionesGetDto;
import com.calero.lili.core.comprobantesWs.services.GetXmlLiquidacionesServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/liquidaciones")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class GetXmlLiquidacionesController {

    private final GetXmlLiquidacionesServiceImpl vtVentasService;

    @GetMapping("xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public CpComprasXMLLiquidacionesGetDto findXMLLiquidacionById(@PathVariable("idEmpresa") Long idEmpresa,
                                                                  @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLLiquidaccionById(idEmpresa, idRecibida);
    }


}
