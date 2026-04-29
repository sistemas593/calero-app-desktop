package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.comprobantesWs.dto.ArchivoDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
import com.calero.lili.core.modVentas.reembolsos.VentasReembolsoRecibidosServiceImpl;
import com.calero.lili.core.modVentas.reembolsos.VtVentasReembolsoPdfXmlServiceImpl;
import com.calero.lili.core.modVentas.reembolsos.VtVentasReembolsoServiceImpl;
import com.calero.lili.core.modVentas.reembolsos.dto.CreationRequestReembolsoDto;
import com.calero.lili.core.modVentas.reembolsos.dto.FilterReembolsoDto;
import com.calero.lili.core.modVentas.reembolsos.dto.GetListDtoTotalizado;
import com.calero.lili.core.modVentas.reembolsos.dto.ResponseReembolsoDto;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/ventas/reembolsos")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class VtVentasReembolsosController {

    private final VtVentasReembolsoServiceImpl vtVentasReembolsoService;
    private final VentasReembolsoRecibidosServiceImpl ventasReembolsoRecibidosService;
    private final AuditorAware<String> auditorAware;
    private final IdDataServiceImpl idDataService;
    private final VtVentasReembolsoPdfXmlServiceImpl vtVentasReembolsoPdfXmlService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_RB_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody CreationRequestReembolsoDto request) {
        return vtVentasReembolsoService.create(idDataService.getIdData(), idEmpresa,
                request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idReembolso}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_RB_MO')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idReembolso") UUID idReembolso,
                              @RequestBody CreationRequestReembolsoDto request) {
        return vtVentasReembolsoService.update(idDataService.getIdData(), idEmpresa,
                idReembolso, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{idReembolso}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('VT_RB_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idReembolso") UUID idReembolso) {
        vtVentasReembolsoService.delete(idDataService.getIdData(), idEmpresa,
                idReembolso, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{idReembolso}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_RB_VR')")
    public ResponseReembolsoDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                         @PathVariable("idReembolso") UUID idReembolso) {
        return vtVentasReembolsoService.findById(idDataService.getIdData(), idEmpresa, idReembolso);
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_RB_VR')")
    public PaginatedDto<ResponseReembolsoDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                              FilterReembolsoDto filters,
                                                              Pageable pageable) {
        return vtVentasReembolsoService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

    @GetMapping("reportes/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_RB_VR')")
    public GetListDtoTotalizado<ResponseReembolsoDto> findAllTotalesPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                                             FilterReembolsoDto filters,
                                                                             Pageable pageable) {
        return vtVentasReembolsoService.findAllPaginateTotalizado(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

    @GetMapping("excel/{idEmpresa}")
    @PreAuthorize("hasAuthority('VT_RB_EX')")
    public void exportarExcel(@PathVariable("idEmpresa") Long idEmpresa, HttpServletResponse response, FilterReembolsoDto filter) throws IOException {
        vtVentasReembolsoService.exportarExcel(idDataService.getIdData(), idEmpresa, response, filter);
    }

    @GetMapping("pdf/{idEmpresa}")
    @PreAuthorize("hasAuthority('VT_RB_EX')")
    public void exportarPDF(@PathVariable("idEmpresa") Long idEmpresa,
                            HttpServletResponse response, FilterReembolsoDto filters) throws DocumentException, IOException {
        vtVentasReembolsoService.exportarPDF(idDataService.getIdData(), idEmpresa, response, filters);
    }

    @PostMapping("recibidos/files/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_RB_IMXML')")
    public CpImpuestosRecibirListCreationResponseDto recibirFiles(@PathVariable("idEmpresa") Long idEmpresa,
                                                                  @Valid @RequestBody List<MultipartFile> documentos) {
        return ventasReembolsoRecibidosService.createFilesVentaReembolso(documentos, idDataService.getIdData(), idEmpresa,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("descargar-pdf/{idEmpresa}/{idRecibida}")
    public ResponseEntity<byte[]> descargarPdfFactura(@PathVariable("idEmpresa") Long idEmpresa,
                                                      @PathVariable("idRecibida") UUID idRecibida) {

        ArchivoDto datos = vtVentasReembolsoPdfXmlService
                .findPDFSustentoLiqReembolsoFacturaById(idDataService.getIdData(), idEmpresa, idRecibida);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + datos.getNombre())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(datos.getContenido().length)
                .body(datos.getContenido());
    }


    @GetMapping("descargar-xml/{idEmpresa}/{idRecibida}")
    public ResponseEntity<byte[]> descargarXmlFactura(@PathVariable("idEmpresa") Long idEmpresa,
                                                      @PathVariable("idRecibida") UUID idRecibida) {


        ArchivoDto datos = vtVentasReembolsoPdfXmlService.
                findXMLSustentoLiqReembolsoFacturaById(idDataService.getIdData(), idEmpresa, idRecibida);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + datos.getNombre())
                .contentType(MediaType.APPLICATION_XML)
                .contentLength(datos.getContenido().length)
                .body(datos.getContenido());
    }

}
