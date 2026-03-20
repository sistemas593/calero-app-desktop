package com.calero.lili.api.controllers;

import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLFacturaGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLNotaCreditoGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLNotaDebitoGetDto;
import com.calero.lili.core.comprobantesWs.services.GetXmlCpImpuestosServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/impuestos")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class GetXmlCpImpuestosController {

    private final GetXmlCpImpuestosServiceImpl vtVentasService;


    @GetMapping("facturas/xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public CpImpuestosXMLFacturaGetDto findXMLFacturaById(@PathVariable("idEmpresa") Long idEmpresa,
                                                          @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLFacturaById(idEmpresa, idRecibida);
    }

    @GetMapping("notas-credito/xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public CpImpuestosXMLNotaCreditoGetDto findXMLNotaCreditoById(@PathVariable("idEmpresa") Long idEmpresa,
                                                                  @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLNotaCreditoById(idEmpresa, idRecibida);
    }

    @GetMapping("notas-debito/xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public CpImpuestosXMLNotaDebitoGetDto findXMLNotaDebitoById(
            @PathVariable("idEmpresa") Long idEmpresa,
            @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLNotaDebitoById(idEmpresa, idRecibida);
    }

}
