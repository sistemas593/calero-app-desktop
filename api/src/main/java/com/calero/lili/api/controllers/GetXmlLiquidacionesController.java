package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpComprasXMLLiquidacionesGetDto;
import com.calero.lili.core.comprobantesWs.services.GetXmlLiquidacionesServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/liquidaciones")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class GetXmlLiquidacionesController {

    private final GetXmlLiquidacionesServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;

    @GetMapping("xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public CpComprasXMLLiquidacionesGetDto findXMLLiquidacionById(@PathVariable("idEmpresa") Long idEmpresa,
                                                                  @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLLiquidaccionById(idDataService.getIdData(), idEmpresa, idRecibida);
    }


}
