package com.calero.lili.api.controllers;

import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.VtVentasXMLFacturaGetDto;
import com.calero.lili.core.comprobantesWs.services.GetXmlVtVentasFacturasServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/ventas")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class GetXmlVtVentasFacturasController {
    private final GetXmlVtVentasFacturasServiceImpl vtVentasService;

    @GetMapping("facturas/xml/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    public VtVentasXMLFacturaGetDto findXMLFacturaById(@PathVariable("idEmpresa") Long idEmpresa,
                                                       @PathVariable("idRecibida") UUID idRecibida) {
        return vtVentasService.findXMLFacturaById(idEmpresa, idRecibida);
    }

    @GetMapping("facturas/descargar-pdf/{idEmpresa}/{idRecibida}")
    public ResponseEntity<byte[]> descargarPdfFactura(@PathVariable("idEmpresa") Long idEmpresa,
                                                      @PathVariable("idRecibida") UUID idRecibida) {

        byte[] pdfBytes = vtVentasService.findPDFFacturaById(idEmpresa, idRecibida); // tu byte[]

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
    }

}
