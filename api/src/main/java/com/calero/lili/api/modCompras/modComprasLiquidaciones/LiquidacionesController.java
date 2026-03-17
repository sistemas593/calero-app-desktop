package com.calero.lili.api.modCompras.modComprasLiquidaciones;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.dto.CreationRequestLiquidacionCompraDto;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.dto.FilterListDto;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.dto.GetDto;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.dto.GetListDto;
import com.calero.lili.api.modCompras.modComprasLiquidaciones.dto.GetListDtoTotalizado;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/liquidaciones")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class LiquidacionesController {

    private final LiquidacionesServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('LQ_LQ_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody CreationRequestLiquidacionCompraDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idLiquidacion}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('LQ_LQ_MO_PR','LQ_LQ_MO_SC','LQ_LQ_MO_TD')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idLiquidacion") UUID idLiquidacion,
                              @RequestBody CreationRequestLiquidacionCompraDto request,
                              FilterListDto filters) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idLiquidacion, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoModificarLiquidacion());
    }

    @DeleteMapping("{idEmpresa}/{idLiquidacion}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('LQ_LQ_EL_PR','LQ_LQ_EL_SC','LQ_LQ_EL_TD')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idLiquidacion") UUID idLiquidacion,
                       FilterListDto filters) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idLiquidacion,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoEliminarLiquidacion());
    }

    @GetMapping("{idEmpresa}/{idLiquidacion}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('LQ_LQ_VR_PR','LQ_LQ_VR_SC','LQ_LQ_VR_TD')")
    public GetDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                           @PathVariable("idLiquidacion") UUID idLiquidacion,
                           FilterListDto filters) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idLiquidacion,
                filters,
                auditorAware.getTipoPermisoVerLiquidacion(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("mensajes/{idEmpresa}/{idLiquidacion}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('LQ_LQ_VR_PR','LQ_LQ_VR_SC','LQ_LQ_VR_TD')")
    public List<Mensajes> findByIdMensajes(@PathVariable("idEmpresa") Long idEmpresa,
                                           @PathVariable("idLiquidacion") UUID idLiquidacion,
                                           FilterListDto filters) {
        return vtVentasService.findByIdMensajes(idDataService.getIdData(), idEmpresa, idLiquidacion,
                filters,
                auditorAware.getTipoPermisoVerLiquidacion(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('LQ_LQ_VR_PR','LQ_LQ_VR_SC','LQ_LQ_VR_TD')")
    public PaginatedDto<GetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                    FilterListDto filters,
                                                    Pageable pageable) {
        return vtVentasService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

    @GetMapping("reportes/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('LQ_LQ_VR_PR','LQ_LQ_VR_SC','LQ_LQ_VR_TD')")
    public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(@PathVariable("idEmpresa") Long idEmpresa,
                                                                      FilterListDto filters,
                                                                      Pageable pageable) {
        log.info("Filters = {}", filters);
        return vtVentasService.findAllPaginateTotalizado(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

    @GetMapping("excel/{idEmpresa}")
    @PreAuthorize("hasAuthority('LQ_LQ_EX')")
    public void exportarExcel(HttpServletResponse response,
                              @PathVariable("idEmpresa") Long idEmpresa,
                              FilterListDto filter) throws IOException {
        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        vtVentasService.exportarExcel(idDataService.getIdData(), idEmpresa, response, filter);
    }

    @GetMapping("pdf/{idEmpresa}")
    @PreAuthorize("hasAuthority('LQ_LQ_EX')")
    public void exportarPDF(HttpServletResponse response,
                            @PathVariable("idEmpresa") Long idEmpresa,
                            FilterListDto filters) throws DocumentException, IOException {
        vtVentasService.exportarPDF(idDataService.getIdData(), idEmpresa, response, filters);
    }

    @PostMapping("anulada/{idEmpresa}/{idLiquidacion}")
    @PreAuthorize("hasAnyAuthority('LQ_LQ_AN_PR','LQ_LQ_AN_SC','LQ_LQ_AN_TD')")
    public ResponseDto updateAnulada(@PathVariable("idEmpresa") Long idEmpresa,
                                     @PathVariable("idLiquidacion") UUID idLiquidacion,
                                     FilterListDto filters) {
        return vtVentasService.updateAnulada(idDataService.getIdData(), idEmpresa, idLiquidacion,
                filters,
                auditorAware.getTipoPermisoAnularLiquidacion(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

}
