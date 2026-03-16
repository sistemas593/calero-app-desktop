package com.calero.lili.api.modCompras.modComprasImpuestos;

import com.calero.lili.api.dtos.deRecibidos.CpImpuestosRecibirCreationRequestDto;
import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.modCompras.modComprasImpuestos.dto.CreationCompraImpuestoRequestDto;
import com.calero.lili.api.modCompras.modComprasImpuestos.dto.FilterListDto;
import com.calero.lili.api.modCompras.modComprasImpuestos.dto.GetDto;
import com.calero.lili.api.modCompras.modComprasImpuestos.dto.GetListDto;
import com.calero.lili.api.modCompras.modComprasImpuestos.dto.GetListDtoTotalizado;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.ResponseDto;
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
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/impuestos")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class CpImpuestosController {

    private final CpImpuestosServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CP_CI_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody CreationCompraImpuestoRequestDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idCompraImpuesto}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CP_CI_MO_PR','CP_CI_MO_SC','CP_CI_MO_TD')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idCompraImpuesto") UUID idCompraImpuesto,
                              @RequestBody CreationCompraImpuestoRequestDto request,
                              FilterListDto filters) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idCompraImpuesto, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoModificarImpuesto());
    }

    @DeleteMapping("facturas/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('CP_CI_EL_PR','CP_CI_EL_SC','CP_CI_EL_TD')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idRecibida") UUID idRecibida,
                       FilterListDto filters) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idRecibida,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoEliminarImpuesto());
    }

    @GetMapping("facturas/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CP_CI_VR_PR','CP_CI_VR_SC','CP_CI_VR_TD')")
    public GetDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                           @PathVariable("idRecibida") UUID idRecibida,
                           FilterListDto filters) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idRecibida,
                filters,
                auditorAware.getTipoPermisoVerImpuesto(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("facturas/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CP_CI_VR_PR','CP_CI_VR_SC','CP_CI_VR_TD')")
    public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(@PathVariable("idEmpresa") Long idEmpresa,
                                                                      FilterListDto filters,
                                                                      Pageable pageable) {
        log.info("Filters = {}", filters);
        return vtVentasService.findAllPaginateTotalizado(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

    @GetMapping("facturas/excel/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_CI_EX')")
    public void exportarExcel(@PathVariable("idEmpresa") Long idEmpresa,
                              FilterListDto filter,
                              HttpServletResponse response) throws IOException {
        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        vtVentasService.exportarExcel(idDataService.getIdData(), idEmpresa, response, filter);
    }

    @GetMapping("facturas/pdf/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_CI_EX')")
    public void exportarPDF(@PathVariable("idEmpresa") Long idEmpresa,
                            FilterListDto filter,
                            HttpServletResponse response) throws IOException {
        vtVentasService.exportarPDF(idDataService.getIdData(), idEmpresa, response, filter);
    }

    @PutMapping("facturas/{idEmpresa}/{idRecibida}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CP_CI_MO_PR','CP_CI_MO_SC','CP_CI_MO_TD')")
    public void updateDatos(@PathVariable("idEmpresa") Long idEmpresa,
                            @PathVariable("idRecibida") UUID idRecibida,
                            @RequestBody CpImpuestosRecibirCreationRequestDto request) {
        vtVentasService.updateDatos(idDataService.getIdData(), idEmpresa, idRecibida, request);
    }

}
