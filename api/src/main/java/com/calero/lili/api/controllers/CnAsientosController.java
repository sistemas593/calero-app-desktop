package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.modContabilidad.modAsientos.CnAsientosServiceImpl;
import com.calero.lili.core.modContabilidad.modAsientos.dto.CreationAsientosRequestDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.FilterAsientoListDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.GetAsientoDto;
import com.calero.lili.core.modContabilidad.modAsientos.dto.GetListDto;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/contabilidad")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class CnAsientosController {

    private final CnAsientosServiceImpl cnAsientosService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CN_AS_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody CreationAsientosRequestDto request) {
        return cnAsientosService.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idAsiento}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CN_AS_MO_PR','CN_AS_MO_SC','CN_AS_MO_TD')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idAsiento") UUID idAsiento,
                              @RequestBody CreationAsientosRequestDto request,
                              FilterAsientoListDto filters) {
        return cnAsientosService.update(idDataService.getIdData(), idEmpresa, idAsiento, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoModificarAsiento());
    }

    @DeleteMapping("{idEmpresa}/{idAsiento}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('CN_AS_EL_PR','CN_AS_EL_SC','CN_AS_EL_TD')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idAsiento") UUID idAsiento,
                       FilterAsientoListDto filters) {
        cnAsientosService.delete(idDataService.getIdData(), idEmpresa, idAsiento,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"),
                filters,
                auditorAware.getTipoPermisoEliminarAsiento());
    }

    @GetMapping("{idEmpresa}/{idAsiento}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CN_AS_VR_PR','CN_AS_VR_SC','CN_AS_VR_TD')")
    public GetAsientoDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                  @PathVariable("idAsiento") UUID idAsiento,
                                  FilterAsientoListDto filters) {
        return cnAsientosService.findById(idDataService.getIdData(), idEmpresa, idAsiento,
                filters,
                auditorAware.getTipoPermisoVerAsiento(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CN_AS_VR_PR','CN_AS_VR_SC','CN_AS_VR_TD')")
    public PaginatedDto<GetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                    FilterAsientoListDto filters,
                                                    Pageable pageable) {

        Sort sort = pageable.getSort().and(Sort.by("idAsiento").ascending());

        int pageSize = pageable.getPageSize();
        if (pageSize > 100) {
            pageSize = 100;}
        Pageable pageableConSort = PageRequest.of(pageable.getPageNumber(), pageSize, sort);

        return cnAsientosService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageableConSort,
                auditorAware.getTipoPermisoVerAsiento(),
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("excel/{idEmpresa}")
    //@PreAuthorize("hasAuthority('VT_FC_EX')")
    public void exportarExcel(HttpServletResponse response,
                              @PathVariable("idEmpresa") Long idEmpresa,
                              FilterAsientoListDto filter) throws IOException {
        String fileName = "Asientos_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        cnAsientosService.exportarExcel(idDataService.getIdData(), idEmpresa, response.getOutputStream(), filter);
    }

    @GetMapping("pdf/{idEmpresa}")
   // @PreAuthorize("hasAuthority('VT_FC_EX')")
    public void exportarPDF(HttpServletResponse response,
                            @PathVariable("idEmpresa") Long idEmpresa,
                            FilterAsientoListDto filters) throws DocumentException, IOException {
        String fileName = "Asientos_" + LocalDateTime.now() + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        cnAsientosService.exportarPDF(idDataService.getIdData(), idEmpresa, response.getOutputStream(), filters);
    }
}
