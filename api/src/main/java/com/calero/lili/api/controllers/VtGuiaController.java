package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.modVentasGuias.VtGuiasServiceImpl;
import com.calero.lili.core.modVentasGuias.dto.CreationRequestGuiaRemisionDto;
import com.calero.lili.core.modVentasGuias.dto.FilterListDto;
import com.calero.lili.core.modVentasGuias.dto.GetDto;
import com.calero.lili.core.modVentasGuias.dto.GetListDto;
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
@RequestMapping(value = "api/v1.0/guias-remision")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class VtGuiaController {

    private final VtGuiasServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_GR_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody CreationRequestGuiaRemisionDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"), "WEB");
    }

    @PutMapping("{idEmpresa}/{idGuia}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_GR_MO_PR','VT_GR_MO_SC','VT_GR_MO_TD')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idGuia") UUID idGuia,
                              @RequestBody CreationRequestGuiaRemisionDto request,
                              FilterListDto filters) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idGuia, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoModificarGuia());
    }

    @DeleteMapping("{idEmpresa}/{idGuia}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('VT_GR_EL_PR','VT_GR_EL_SC','VT_GR_EL_TD')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idGuia") UUID idGuia,
                       FilterListDto filters) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idGuia,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoEliminarGuia());
    }

    @GetMapping("{idEmpresa}/{idGuia}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_GR_VR_PR','VT_GR_VR_SC','VT_GR_VR_TD')")
    public GetDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                           @PathVariable("idGuia") UUID idGuia,
                           FilterListDto filters) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idGuia,
                filters,
                auditorAware.getTipoPermisoVerGuia(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("mensajes/{idEmpresa}/{idGuia}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_GR_VR_PR','VT_GR_VR_SC','VT_GR_VR_TD')")
    public List<Mensajes> findByIdMensajes(@PathVariable("idEmpresa") Long idEmpresa,
                                           @PathVariable("idGuia") UUID idGuia,
                                           FilterListDto filters) {
        return vtVentasService.findByIdMensajes(idDataService.getIdData(), idEmpresa, idGuia,
                filters,
                auditorAware.getTipoPermisoVerGuia(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_GR_VR_PR','VT_GR_VR_SC','VT_GR_VR_TD')")
    public PaginatedDto<GetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                    FilterListDto filters,
                                                    Pageable pageable) {
        return vtVentasService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable,
                auditorAware.getTipoPermisoVerGuia(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("excel/{idEmpresa}")
    @PreAuthorize("hasAuthority('VT_GR_EX')")
    public void exportarExcel(HttpServletResponse response,
                              @PathVariable("idEmpresa") Long idEmpresa,
                              FilterListDto filter) throws IOException {
        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        vtVentasService.exportarExcel(idDataService.getIdData(), idEmpresa, response, filter);
    }

    @GetMapping("pdf/{idEmpresa}")
    @PreAuthorize("hasAuthority('VT_GR_EX')")
    public void exportarPDF(HttpServletResponse response,
                            @PathVariable("idEmpresa") Long idEmpresa,
                            FilterListDto filters) throws DocumentException, IOException {
        vtVentasService.exportarPDF(idDataService.getIdData(), idEmpresa, response, filters);
    }

    @PostMapping("anulada/{idEmpresa}/{idVenta}")
    @PreAuthorize("hasAnyAuthority('VT_GR_AN_PR','VT_GR_AN_SC','VT_GR_AN_TD')")
    public ResponseDto updateAnulada(@PathVariable("idEmpresa") Long idEmpresa,
                                     @PathVariable("idVenta") UUID idVenta,
                                     FilterListDto filters) {
        return vtVentasService.updateAnulada(idDataService.getIdData(), idEmpresa, idVenta,
                filters,
                auditorAware.getTipoPermisoAnularGuia(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

}
