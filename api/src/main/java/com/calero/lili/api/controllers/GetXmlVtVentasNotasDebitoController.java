package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLNotaDebitoGetDto;
import com.calero.lili.core.comprobantesWs.services.GetXmlVtVentasNotasDebitoServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/ventas")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class GetXmlVtVentasNotasDebitoController {
    private final GetXmlVtVentasNotasDebitoServiceImpl vtVentasService;
    private IdDataServiceImpl idDataService;

    @GetMapping("notas-debito/xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public VtVentasXMLNotaDebitoGetDto findXMLNotaDebitoById(@PathVariable("idEmpresa") Long idEmpresa,
                                                             @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLNotaDebitoById(idDataService.getIdData(), idEmpresa, idRecibida);
    }

}
