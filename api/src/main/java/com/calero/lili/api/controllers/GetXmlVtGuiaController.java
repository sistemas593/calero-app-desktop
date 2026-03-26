package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLGuiaRemisionGetDto;
import com.calero.lili.core.comprobantesWs.services.GetXmlVtGuiasServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/guias-remision")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class GetXmlVtGuiaController {

    private final GetXmlVtGuiasServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;

    @GetMapping("xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public VtVentasXMLGuiaRemisionGetDto findXMLGuiaRemisionById(@PathVariable("idEmpresa") Long idEmpresa,
                                                                 @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLGuiaById(idDataService.getIdData(), idEmpresa, idRecibida);
    }


}
