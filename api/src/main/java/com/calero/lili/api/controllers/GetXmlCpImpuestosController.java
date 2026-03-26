package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLFacturaGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLNotaCreditoGetDto;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.CpImpuestosXMLNotaDebitoGetDto;
import com.calero.lili.core.comprobantesWs.services.GetXmlCpImpuestosServiceImpl;
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
@RequestMapping(value = "api/v1.0/impuestos")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class GetXmlCpImpuestosController {

    private final GetXmlCpImpuestosServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;


    @GetMapping("facturas/xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public CpImpuestosXMLFacturaGetDto findXMLFacturaById(@PathVariable("idEmpresa") Long idEmpresa,
                                                          @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLFacturaById(idDataService.getIdData(), idEmpresa, idRecibida);
    }

    @GetMapping("notas-credito/xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public CpImpuestosXMLNotaCreditoGetDto findXMLNotaCreditoById(@PathVariable("idEmpresa") Long idEmpresa,
                                                                  @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLNotaCreditoById(idDataService.getIdData(), idEmpresa, idRecibida);
    }

    @GetMapping("notas-debito/xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public CpImpuestosXMLNotaDebitoGetDto findXMLNotaDebitoById(
            @PathVariable("idEmpresa") Long idEmpresa,
            @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLNotaDebitoById(idDataService.getIdData(), idEmpresa, idRecibida);
    }

}
