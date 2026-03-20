package com.calero.lili.api.controllers;

import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLRetencionGetDto;
import com.calero.lili.core.comprobantesWs.services.GetXmlVentasRetencionesServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/ventas/comprobantes-retencion")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class GetXmlVentasRetencionesController {

    private final GetXmlVentasRetencionesServiceImpl vtVentasService;


    @GetMapping("xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public VtVentasXMLRetencionGetDto findXMLRetencionById(@PathVariable("idEmpresa") Long idEmpresa,
                                                           @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLRetencionById(idEmpresa, idRecibida);
    }


}
