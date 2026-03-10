package com.calero.lili.api.modContabilidad.modReportes;


import com.calero.lili.api.modContabilidad.modAsientos.dto.FilterListDto;
import com.calero.lili.api.modContabilidad.modAsientos.dto.LibroDiarioDto;
import com.calero.lili.api.modContabilidad.modPlanCuentas.dto.CnPlanCuentaListFilterDto;
import com.calero.lili.api.modContabilidad.modReportes.dto.BalanceValoresDto;
import com.calero.lili.api.modContabilidad.modReportes.dto.MayorGeneralDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/contabilidad/reportes")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class CnReportesController {


    private final IdDataServiceImpl idDataService;
    private final CnBalanceComprobacionServiceImpl cnBalanceComprobacionService;
    private final CnAsientosComprobanteDiario cnAsientosComprobanteDiario;
    private final CnReporteMayorGeneralServiceImpl reporteMayorGeneral;
    private final CnLibroDiarioServiceImpl cnLibroDiarioService;
    private final CnEstadoSituacionFinancieraServiceImpl cnEstadoSituacionFinancieraService;
    private final CnReporteEstadoPerdidasGananciasServiceImpl cnReporteEstadoPerdidasGananciasService;
    private final CnEstadoSituacionComparativoServiceImpl cnEstadoSituacionComparativoService;
    private final CnEstadoPerdidasGananciasComparativoServiceImpl cnEstadoPerdidasGananciasComparativoService;


    @GetMapping("balance-comprobacion/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_BL')")
    public PaginatedDto<BalanceValoresDto> getBalanceComprobacion(@PathVariable("idEmpresa") Long idEmpresa,
                                                                  CnPlanCuentaListFilterDto filters,
                                                                  Pageable pageable) {

        return cnBalanceComprobacionService.getBalanceComprobacion(idDataService.getIdData(), idEmpresa, filters, pageable);

    }


    @GetMapping(value = "balance-comprobacion/pdf/{idEmpresa}", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_BL')")
    public ResponseEntity<byte[]> generarBalanceComprobacionPdf(@PathVariable("idEmpresa") Long idEmpresa,
                                                                CnPlanCuentaListFilterDto filters) {

        byte[] pdf = cnBalanceComprobacionService.getBalanceComprobacionPdf(idDataService.getIdData(), idEmpresa, filters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String nameDocument = "reporte-balance-comprobacion" + "_" + LocalDateTime.now();
        headers.setContentDispositionFormData("filename", nameDocument + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

    }

    @GetMapping("comprobante-diario/pdf/{idEmpresa}/{idAsiento}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CN_AS_RP')")
    public void getComprobanteDiarioPdf(HttpServletResponse response,
                                        @PathVariable("idEmpresa") Long idEmpresa,
                                        @PathVariable("idAsiento") UUID idAsiento) {
        cnAsientosComprobanteDiario.generateComprobanteDiario(idDataService.getIdData(), idEmpresa, idAsiento, response);
    }


    @GetMapping("mayor-general/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_RP')")
    public MayorGeneralDto reporteMayorGeneral(@PathVariable("idEmpresa") Long idEmpresa,
                                               FilterListDto filters,
                                               Pageable pageable) {
        return reporteMayorGeneral.reporteMayorGeneral(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

    @GetMapping("mayor-general/excel/{idEmpresa}")
    @PreAuthorize("hasAuthority('CN_AS_RP')")
    public void reporteMayorGeneralExportarExcel(HttpServletResponse response, @PathVariable("idEmpresa") Long idEmpresa,
                                                 FilterListDto filters) throws IOException {
        reporteMayorGeneral.reporteMayorGeneralExcel(idDataService.getIdData(), idEmpresa, response, filters);
    }

    @GetMapping("mayor-general/pdf/{idEmpresa}")
    @PreAuthorize("hasAuthority('CN_AS_RP')")
    public ResponseEntity<byte[]> reporteMayorGeneralPdf(HttpServletResponse response, @PathVariable("idEmpresa") Long idEmpresa,
                                                         FilterListDto filters) throws IOException {

        byte[] pdf = reporteMayorGeneral.reporteMayorGeneralPdf(idDataService.getIdData(), idEmpresa, filters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String nameDocument = "reporte-mayor-general" + "_" + LocalDateTime.now();
        headers.setContentDispositionFormData("filename", nameDocument + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }


    @GetMapping("libro-diario/excel/{idEmpresa}")
    @PreAuthorize("hasAuthority('CN_AS_RP')")
    public void reporteLibroDiarioExportarExcel(HttpServletResponse response, @PathVariable("idEmpresa") Long idEmpresa,
                                                FilterListDto filters) throws IOException {
        cnLibroDiarioService.reportLibroDiarioExcel(idDataService.getIdData(), idEmpresa, response, filters);
    }


    @GetMapping(value = "libro-diario/pdf/{idEmpresa}", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_RP')")
    public ResponseEntity<byte[]> generarRetencionFuente(@PathVariable("idEmpresa") Long idEmpresa,
                                                         FilterListDto filters) throws IOException {

        byte[] pdf = cnLibroDiarioService.generarReporteLibroDiarioPDF(idDataService.getIdData(), idEmpresa, filters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String nameDocument = "reporte-libro-diario" + "_" + LocalDateTime.now();
        headers.setContentDispositionFormData("filename", nameDocument + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

    }

    @GetMapping("libro-diario/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_RP')")
    public PaginatedDto<LibroDiarioDto> reporteLibroDiario(@PathVariable("idEmpresa") Long idEmpresa,
                                                           FilterListDto filters,
                                                           Pageable pageable) {
        return cnLibroDiarioService.reportLibroDiario(idDataService.getIdData(), idEmpresa, filters, pageable);
    }


    @GetMapping("estado-financiero/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_BL')")
    public PaginatedDto<BalanceValoresDto> reporteEstadoFinancieroPageable(@PathVariable("idEmpresa") Long idEmpresa,
                                                                           CnPlanCuentaListFilterDto filters,
                                                                           Pageable pageable) {
        return cnEstadoSituacionFinancieraService.getReporteEstadoFinancieroPaginado(idDataService.getIdData(), idEmpresa, filters, pageable);

    }

    @GetMapping("estado-financiero/pdf/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_BL')")
    public ResponseEntity<byte[]> reporteEstadoFinancieroPdf(@PathVariable("idEmpresa") Long idEmpresa,
                                                             CnPlanCuentaListFilterDto filters) {

        byte[] pdf = cnEstadoSituacionFinancieraService.getReporteEstadoFinancieroPdf(idDataService.getIdData(), idEmpresa, filters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String nameDocument = "reporte-estado-financiero" + "_" + LocalDateTime.now();
        headers.setContentDispositionFormData("filename", nameDocument + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

    }


    @GetMapping("estado-resultados/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_BL')")
    public PaginatedDto<BalanceValoresDto> reporteEstadoResultadosPageable(@PathVariable("idEmpresa") Long idEmpresa,
                                                                           CnPlanCuentaListFilterDto filters,
                                                                           Pageable pageable) {
        return cnReporteEstadoPerdidasGananciasService.getReporteEstadoPerdidasGanancias(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

    @GetMapping("estado-resultados/pdf/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_BL')")
    public ResponseEntity<byte[]> reporteEstadoResultadosPdf(@PathVariable("idEmpresa") Long idEmpresa,
                                                             CnPlanCuentaListFilterDto filters) {

        byte[] pdf = cnReporteEstadoPerdidasGananciasService.getReporteEstadoPerdidasGananciasPdf(idDataService.getIdData(), idEmpresa, filters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String nameDocument = "reporte-estado-resultados" + "_" + LocalDateTime.now();
        headers.setContentDispositionFormData("filename", nameDocument + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

    }

    @GetMapping("estado-resultados-comparativo/pdf/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_BL')")
    public ResponseEntity<byte[]> reporteEstadoResultadosComparativoPdf(@PathVariable("idEmpresa") Long idEmpresa,
                                                                        CnPlanCuentaListFilterDto filters) {

        byte[] pdf = cnEstadoPerdidasGananciasComparativoService.getReporteEstadoPerdidasGananciasComparativoPdf
                (idDataService.getIdData(), idEmpresa, filters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String nameDocument = "reporte-estado-resultados-comparativo" + "_" + LocalDateTime.now();
        headers.setContentDispositionFormData("filename", nameDocument + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

    }


    @GetMapping("estado-financiero-comparativo/pdf/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_BL')")
    public ResponseEntity<byte[]> reporteEstadoFinancieroComparativoPdf(@PathVariable("idEmpresa") Long idEmpresa,
                                                                        CnPlanCuentaListFilterDto filters) {

        byte[] pdf = cnEstadoSituacionComparativoService.getReporteEstadoFinancieroPdfComparativo(idDataService.getIdData(), idEmpresa, filters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String nameDocument = "reporte-estado-financiero-comparativo" + "_" + LocalDateTime.now();
        headers.setContentDispositionFormData("filename", nameDocument + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

    }

}
