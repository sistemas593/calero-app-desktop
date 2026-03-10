package com.calero.lili.api.modImpuestosProcesos;

import com.calero.lili.api.dtos.FilterImpuestoDto;
import com.calero.lili.api.modCompras.dto.FilterDto;
import com.calero.lili.api.modCompras.service.AtsService;
import com.calero.lili.api.modImpuestosAnexos.GenerarDeclaracionImpuestoService;
import com.calero.lili.api.modImpuestosAnexos.ImpuestosServicesImpl;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.ImpuestosF103Dto;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.ImpuestosF104Dto;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.ImpuestosF107Dto;
import com.calero.lili.api.modImpuestosProcesos.dto.impuestos.ValoresTalonResumenDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/impuestos")
@CrossOrigin(originPatterns = "*")
public class ImpuestosController {


    private final AtsService atsService;
    private final GenerarDeclaracionImpuestoService generarDeclaracionImpuestoService;
    private final IdDataServiceImpl idDataService;
    private final ImpuestosServicesImpl impuestosServices;


    @GetMapping("formulario103/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('IM_F103_VR')")
    public ImpuestosF103Dto generarF103(@PathVariable("idEmpresa") Long idEmpresa,
                                        FilterImpuestoDto request) {
        return impuestosServices.setearFImpuestosF103();
    }


    @GetMapping("formulario103/pdf/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('IM_F103_VR')")
    public void generarFormularioPdfF103(HttpServletResponse response,
                                         @PathVariable("idEmpresa") Long idEmpresa,
                                         FilterImpuestoDto request) {
        generarDeclaracionImpuestoService.generarPdfDeclaracionRetenciones(impuestosServices.setearFImpuestosF103(),
                response, idEmpresa, request);
    }

    @GetMapping("formulario104/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('IM_F104_VR')")
    public ImpuestosF104Dto generarF104(@PathVariable("idEmpresa") Long idEmpresa,
                                        FilterImpuestoDto request) {
        return impuestosServices.setearImpuestosF104(request, idDataService.getIdData(), idEmpresa);
    }

    @GetMapping("formulario104/pdf/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('IM_F104_VR')")
    public void generarFormularioPdfF104(HttpServletResponse response,
                                         @PathVariable("idEmpresa") Long idEmpresa,
                                         FilterImpuestoDto request) {
        generarDeclaracionImpuestoService.generarPdfDeclaracionImpuestos(impuestosServices
                .setearImpuestosF104(request, idDataService.getIdData(), idEmpresa), response);
    }

    @GetMapping("formulario107/{idEmpresa}/{idTrabajador}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('IM_F107_VR')")
    public ImpuestosF107Dto generarF107(@PathVariable("idEmpresa") Long idEmpresa,
                                        @PathVariable("idTrabajador") UUID idTrabajador,
                                        FilterImpuestoDto request) {
        return impuestosServices.setearImpuestosF107(idDataService.getIdData(), idEmpresa, idTrabajador);
    }

    @GetMapping("formulario107/pdf/{idEmpresa}/{idTrabajador}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('IM_F107_VR')")
    public void generarFormularioPdf107(HttpServletResponse response,
                                        @PathVariable("idEmpresa") Long idEmpresa,
                                        @PathVariable("idTrabajador") UUID idTrabajador,
                                        FilterImpuestoDto request) {
        generarDeclaracionImpuestoService.generarFormulario107(impuestosServices.setearImpuestosF107(idDataService.getIdData(), idEmpresa, idTrabajador),
                response);
    }


    @GetMapping(value = "talon-resumen-retencion-fuente/pdf/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('IM_TR_VR')")
    public ResponseEntity<byte[]> generarRetencionFuente(@PathVariable("idEmpresa") Long idEmpresa,
                                                         FilterImpuestoDto request) {


        Long idData = idDataService.getIdData();

        ValoresTalonResumenDto dto = impuestosServices.setearRetencionAlaFuente(request, idData, idEmpresa);

        byte[] pdf = generarDeclaracionImpuestoService.generarReporteTalonResumenRetencionFuentePDF(idData, idEmpresa, dto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String nameDocument = "reporte_retencion_fuente" + "_" + LocalDateTime.now();
        headers.setContentDispositionFormData("filename", nameDocument + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

    }


    // TODO FECHA DESDE Y HASTA, TODO EL AÑO ENERO A DICIEMBRE
    @PostMapping("talon-resumen-retencion-fuente/xml/{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('IM_TR_VR')")
    public void generarRetencionFuenteXml(HttpServletResponse response,
                                          @PathVariable("idEmpresa") Long idEmpresa,
                                          FilterImpuestoDto request) {
        generarDeclaracionImpuestoService
                .generarReporteTalonResumenRetencionFuenteXml(
                        impuestosServices.setearRetencionFuenteXml(idDataService.getIdData(), idEmpresa, request), response);
    }


    @PostMapping("/xml/ats/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('IM_AT_VR')")
    public void createAtsXml(HttpServletResponse response,
                             @PathVariable("idEmpresa") Long idEmpresa,
                             FilterDto request) {
        atsService.generateDocumentoAtsXml(idDataService.getIdData(), idEmpresa, request, response);
    }

    @PostMapping("/pdf/ats/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('IM_AT_VR')")
    public void createAtsPdf(HttpServletResponse response,
                             @PathVariable("idEmpresa") Long idEmpresa,
                             FilterDto request) {
        atsService.generarTalonResumen(idDataService.getIdData(), idEmpresa, request, response);
    }


}
