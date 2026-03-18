package com.calero.lili.api.controllers;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
import com.calero.lili.core.modVentas.facturas.dto.FilterListDto;
import com.calero.lili.core.modVentas.reembolsos.VentasReembolsoRecibidosServiceImpl;
import com.calero.lili.core.modVentas.reembolsos.VtVentasReembolsoServiceImpl;
import com.calero.lili.core.modVentas.reembolsos.dto.CreationRequestReembolsoDto;
import com.calero.lili.core.modVentas.reembolsos.dto.GetListDtoTotalizado;
import com.calero.lili.core.modVentas.reembolsos.dto.ResponseReembolsoDto;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    @PostMapping("")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_RB_CR')")
    public ResponseDto create(@Valid @RequestBody CreationRequestReembolsoDto request) {
        return vtVentasReembolsoService.create(request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idReembolso}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_RB_MO')")
    public ResponseDto update(@PathVariable("idReembolso") UUID idReembolso,
                              @RequestBody CreationRequestReembolsoDto request) {
        return vtVentasReembolsoService.update(idReembolso, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idReembolso}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('VT_RB_EL')")
    public void delete(@PathVariable("idReembolso") UUID idReembolso) {
        vtVentasReembolsoService.delete(idReembolso, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idReembolso}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_RB_VR')")
    public ResponseReembolsoDto findById(@PathVariable("idReembolso") UUID idReembolso) {
        return vtVentasReembolsoService.findById(idReembolso);
    }

    @GetMapping("/paginado")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_RB_VR')")
    public PaginatedDto<ResponseReembolsoDto> findAllPaginate(FilterListDto filters,
                                                              Pageable pageable) {
        return vtVentasReembolsoService.findAllPaginate(filters, pageable);
    }

    @GetMapping("/reportes")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_RB_VR')")
    public GetListDtoTotalizado<ResponseReembolsoDto> findAllTotalesPaginate(FilterListDto filters,
                                                                             Pageable pageable) {
        return vtVentasReembolsoService.findAllPaginateTotalizado(filters, pageable);
    }

    @GetMapping("/excel")
    @PreAuthorize("hasAuthority('VT_RB_EX')")
    public void exportarExcel(HttpServletResponse response, FilterListDto filter) throws IOException {
        vtVentasReembolsoService.exportarExcel(response, filter);
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasAuthority('VT_RB_EX')")
    public void exportarPDF(HttpServletResponse response, FilterListDto filters) throws DocumentException, IOException {
        vtVentasReembolsoService.exportarPDF(response, filters);
    }

    @PostMapping("recibidos/files")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_RB_IMXML')")
    public CpImpuestosRecibirListCreationResponseDto recibirFiles(@Valid @RequestBody List<MultipartFile> documentos) {
        return ventasReembolsoRecibidosService.createFilesVentaReembolso(documentos);
    }

}
