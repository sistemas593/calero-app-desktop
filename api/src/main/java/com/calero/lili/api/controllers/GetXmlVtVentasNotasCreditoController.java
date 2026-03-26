package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLNotaCreditoGetDto;
import com.calero.lili.core.comprobantesWs.services.GetXmlVtVentasNotasCreditoServiceImpl;
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
@RequestMapping(value = "api/v1.0/ventas")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class GetXmlVtVentasNotasCreditoController {
    private final GetXmlVtVentasNotasCreditoServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;

    @GetMapping("notas-credito/xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public VtVentasXMLNotaCreditoGetDto findXMLNotaCreditoById(@PathVariable("idEmpresa") Long idEmpresa,
                                                               @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLNotaCreditoById(idDataService.getIdData(), idEmpresa, idRecibida);
    }


}
