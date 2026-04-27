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

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('LQ_LQR_CR')")
    public ResponseDto create(@Valid @RequestBody ReembolsoRequestDto request,
                              @PathVariable("idEmpresa") Long idEmpresa) {
        return reembolsosService.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idReembolso}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('LQ_LQR_MO_PR','LQ_LQR_MO_SC','LQ_LQR_MO_TD')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idReembolso") UUID idReembolso,
                              @Valid @RequestBody ReembolsoRequestDto request,
                              FilterListDto filters) {

        return reembolsosService.update(idDataService.getIdData(), idEmpresa, idReembolso, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"), filters,
                auditorAware.getTipoPermisoModificarReembolso());
    }

    @DeleteMapping("{idEmpresa}/{idReembolso}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('LQ_LQR_EL_PR','LQ_LQR_EL_SC','LQ_LQR_EL_TD')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idReembolso") UUID idReembolso,
                       FilterListDto filters) {

        reembolsosService.delete(idDataService.getIdData(), idEmpresa, idReembolso,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoEliminarReembolso());
    }

    @GetMapping("{idEmpresa}/{idReembolso}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('LQ_LQR_VR_PR','LQ_LQR_VR_SC','LQ_LQR_VR_TD')")
    public GetReembolsoDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                    @PathVariable("idReembolso") UUID idReembolso,
                                    FilterListDto filters) {

        return reembolsosService.findById(idDataService.getIdData(), idEmpresa, idReembolso, filters,
                auditorAware.getTipoPermisoVerReembolso(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('LQ_LQR_VR_PR','LQ_LQR_VR_SC','LQ_LQR_VR_TD')")
    public PaginatedDto<GetReembolsoDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                         FilterListDto filters, Pageable pageable) {

        return reembolsosService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable,
                auditorAware.getTipoPermisoVerReembolso(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("reportes/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('LQ_LQR_VR_PR','LQ_LQR_VR_SC','LQ_LQR_VR_TD')")
    public GetListDtoTotalizado<GetReembolsoDto> findAllPaginateTotalizado(@PathVariable("idEmpresa") Long idEmpresa,
                                                                           FilterListDto filters,
                                                                           Pageable pageable) {
        return reembolsosService.findAllPaginateTotalizado(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

    @GetMapping("excel/{idEmpresa}")
    @PreAuthorize("hasAuthority('LQ_LQR_EX')")
    public void exportarExcel(@PathVariable("idEmpresa") Long idEmpresa,
                              HttpServletResponse response, FilterListDto filter) throws IOException {
        reembolsosService.exportarExcel(idDataService.getIdData(), idEmpresa, response, filter);
    }

    @GetMapping("pdf/{idEmpresa}")
    @PreAuthorize("hasAuthority('LQ_LQR_EX')")
    public void exportarPDF(@PathVariable("idEmpresa") Long idEmpresa,
                            HttpServletResponse response, FilterListDto filters) throws DocumentException, IOException {
        reembolsosService.exportarPDF(idDataService.getIdData(), idEmpresa, response, filters);
    }

    @PostMapping("recibidos/files/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('LQ_LQR_IMXML')")
    public CpImpuestosRecibirListCreationResponseDto recibirFiles(@PathVariable("idEmpresa") Long idEmpresa,
                                                                  @Valid @RequestBody List<MultipartFile> documentos) {
        return documentoRecibidosService.createFilesLiqReembolso(idDataService.getIdData(), idEmpresa, documentos,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

}
