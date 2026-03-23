package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.comprobantesWs.RespuestaProcesoGetDto;
import com.calero.lili.core.comprobantesWs.services.ProcesarDocumentosServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/procesar-documentos")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

// TODO REVISAR

public class ProcesarDocumentosController {

    @Autowired
    private ProcesarDocumentosServiceImpl procesarDocumentosService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("ventas/{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public RespuestaProcesoGetDto procesarFacNcNd(@PathVariable("idEmpresa") Long idEmpresa,
                                                  @PathVariable("id") UUID id) {
        return procesarDocumentosService.procesarFacNcNd(idDataService.getIdData(), idEmpresa, id, "WEB");

    }

    @PostMapping("guias-remision/{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public RespuestaProcesoGetDto procesarGuiaRemision(
            @PathVariable("idEmpresa") Long idEmpresa,
            @PathVariable("id") UUID id) {
        return procesarDocumentosService.procesarGuiaRemision(idDataService.getIdData(), idEmpresa, id);
    }

    @PostMapping("liquidaciones/{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public RespuestaProcesoGetDto procesarLiquidacion(
            @PathVariable("idEmpresa") Long idEmpresa,
            @PathVariable("id") UUID id) {
        return procesarDocumentosService.procesarLiquidacion(idDataService.getIdData(), idEmpresa, id);
    }

    @PostMapping("comprobantes-retencion/{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public RespuestaProcesoGetDto procesarComprobanteRetencion(
            @PathVariable("idEmpresa") Long idEmpresa,
            @PathVariable("id") UUID id) {
        return procesarDocumentosService.procesarComprobanteRetencion(idDataService.getIdData(), idEmpresa, id);
    }

//    @PostMapping()
//    @ResponseStatus(code = HttpStatus.OK)
//    public void procesarTodos() {
//        procesarDocumentosService.procesarTodos();
//    }

}
