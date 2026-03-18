package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.comprobantes.DocumentoRecibidosServiceImpl;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.FilterListDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.GetListDtoTotalizado;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.LiquidacionesReembolsosServiceImpl;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.dto.GetReembolsoDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.dto.ReembolsoRequestDto;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping(value = "api/v1.0/liquidaciones/reembolsos")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class LiquidacionReembolsosController {

    private final LiquidacionesReembolsosServiceImpl reembolsosService;
    private final DocumentoRecibidosServiceImpl documentoRecibidosService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('LQ_LQR_CR')")
    public ResponseDto create(@Valid @RequestBody ReembolsoRequestDto request) {
        return reembolsosService.create(request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/update/{idReembolso}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('LQ_LQR_MO_PR','LQ_LQR_MO_SC','LQ_LQR_MO_TD')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idReembolso") UUID idReembolso,
                              @Valid @RequestBody ReembolsoRequestDto request,
                              FilterListDto filters) {
        return reembolsosService.update(idEmpresa, idReembolso, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoModificarReembolso());
    }

    @DeleteMapping("{idEmpresa}/delete/{idReembolso}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('LQ_LQR_EL_PR','LQ_LQR_EL_SC','LQ_LQR_EL_TD')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idReembolso") UUID idReembolso,
                       FilterListDto filters) {
        reembolsosService.delete(idEmpresa, idReembolso,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoEliminarReembolso());
    }

    @GetMapping("{idEmpresa}/findById/{idReembolso}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('LQ_LQR_VR_PR','LQ_LQR_VR_SC','LQ_LQR_VR_TD')")
    public GetReembolsoDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                    @PathVariable("idReembolso") UUID idReembolso,
                                    FilterListDto filters) {
        return reembolsosService.findById(idEmpresa, idReembolso,
                filters,
                auditorAware.getTipoPermisoVerReembolso(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("/findAllPaginate")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('LQ_LQR_VR_PR','LQ_LQR_VR_SC','LQ_LQR_VR_TD')")
    public PaginatedDto<GetReembolsoDto> findAllPaginate(FilterListDto filters, Pageable pageable) {
        return reembolsosService.findAllPaginate(filters, pageable,
                auditorAware.getTipoPermisoVerReembolso(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("/reportes")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('LQ_LQR_VR_PR','LQ_LQR_VR_SC','LQ_LQR_VR_TD')")
    public GetListDtoTotalizado<GetReembolsoDto> findAllPaginateTotalizado(FilterListDto filters,
                                                                           Pageable pageable) {
        return reembolsosService.findAllPaginateTotalizado(filters, pageable);
    }

    @GetMapping("/excel")
    @PreAuthorize("hasAuthority('LQ_LQR_EX')")
    public void exportarExcel(HttpServletResponse response, FilterListDto filter) throws IOException {
        reembolsosService.exportarExcel(response, filter);
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasAuthority('LQ_LQR_EX')")
    public void exportarPDF(HttpServletResponse response, FilterListDto filters) throws DocumentException, IOException {
        reembolsosService.exportarPDF(response, filters);
    }

    @PostMapping("recibidos/files")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('LQ_LQR_IMXML')")
    public CpImpuestosRecibirListCreationResponseDto recibirFiles(@Valid @RequestBody List<MultipartFile> documentos) {
        return documentoRecibidosService.createFilesLiqReembolso(idDataService.getIdData(), documentos);
    }

}
