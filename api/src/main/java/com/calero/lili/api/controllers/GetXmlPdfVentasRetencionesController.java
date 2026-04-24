package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLRetencionGetDto;
import com.calero.lili.core.comprobantesWs.dto.ArchivoDto;
import com.calero.lili.core.comprobantesWs.services.GetXmlVentasRetencionesServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/ventas/comprobantes-retencion")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class GetXmlPdfVentasRetencionesController {

    private final GetXmlVentasRetencionesServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;


    @GetMapping("xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public VtVentasXMLRetencionGetDto findXMLRetencionById(@PathVariable("idEmpresa") Long idEmpresa,
                                                           @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLRetencionById(idDataService.getIdData(), idEmpresa, idRecibida);
    }

    @GetMapping("descargar-pdf/{idEmpresa}/{idRecibida}")
    public ResponseEntity<byte[]> descargarPdfFactura(@PathVariable("idEmpresa") Long idEmpresa,
                                                      @PathVariable("idRecibida") UUID idRecibida) {

        ArchivoDto datos = vtVentasService.findPDFRetencionById(idDataService.getIdData(), idEmpresa, idRecibida);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + datos.getNombre())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(datos.getContenido().length)
                .body(datos.getContenido());
    }


    @GetMapping("descargar-xml/{idEmpresa}/{idRecibida}")
    public ResponseEntity<byte[]> descargarXmlFactura(@PathVariable("idEmpresa") Long idEmpresa,
                                                      @PathVariable("idRecibida") UUID idRecibida) {


        ArchivoDto datos = vtVentasService.findFileXMLRetencion(idDataService.getIdData(), idEmpresa, idRecibida);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + datos.getNombre())
                .contentType(MediaType.APPLICATION_XML)
                .contentLength(datos.getContenido().length)
                .body(datos.getContenido());
    }

}
