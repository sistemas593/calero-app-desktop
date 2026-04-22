package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLNotaDebitoGetDto;
import com.calero.lili.core.comprobantesWs.dto.ArchivoDto;
import com.calero.lili.core.comprobantesWs.services.GetXmlVtVentasNotasDebitoServiceImpl;
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
@RequestMapping(value = "api/v1.0/ventas")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class GetXmlPdfVtVentasNotasDebitoController {

    private final GetXmlVtVentasNotasDebitoServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;

    @GetMapping("notas-debito/xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public VtVentasXMLNotaDebitoGetDto findXMLNotaDebitoById(@PathVariable("idEmpresa") Long idEmpresa,
                                                             @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLNotaDebitoById(idDataService.getIdData(), idEmpresa, idRecibida);
    }

    @GetMapping("notas-debito/descargar-pdf/{idEmpresa}/{idRecibida}")
    public ResponseEntity<byte[]> descargarPdfNotaDebito(@PathVariable("idEmpresa") Long idEmpresa,
                                                         @PathVariable("idRecibida") UUID idRecibida) {

        ArchivoDto datos = vtVentasService.findPDFNotaDebitoById(idDataService.getIdData(), idEmpresa, idRecibida, "WEB"); // tu byte[]

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + datos.getNombre())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(datos.getContenido().length)
                .body(datos.getContenido());
    }


    @GetMapping("notas-debito/descargar-xml/{idEmpresa}/{idRecibida}")
    public ResponseEntity<byte[]> descargarXmlNotaDebito(@PathVariable("idEmpresa") Long idEmpresa,
                                                         @PathVariable("idRecibida") UUID idRecibida) {


        ArchivoDto datos = vtVentasService.findFileXMLNotaDebito(idDataService.getIdData(), idEmpresa, idRecibida); // tu byte[]

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + datos.getNombre())
                .contentType(MediaType.APPLICATION_XML)
                .contentLength(datos.getContenido().length)
                .body(datos.getContenido());
    }


}
