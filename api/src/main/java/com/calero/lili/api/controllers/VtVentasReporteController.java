package com.calero.lili.api.controllers;


import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.modVentas.dto.GetListDto;
import com.calero.lili.core.modVentas.dto.GetListDtoTotalizado;
import com.calero.lili.core.modVentas.facturas.VtVentasFacturasExcelService;
import com.calero.lili.core.modVentas.facturas.VtVentasFacturasServiceImpl;
import com.calero.lili.core.modVentas.facturas.dto.FilterListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/ventas")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class VtVentasReporteController {

    private final VtVentasFacturasServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;
    private final VtVentasFacturasExcelService vtVentasFacturasExcelService;
    private final AuditorAwareImpl auditorAware;

    @GetMapping("reportes/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('VT_FC_VR_PR','VT_FC_VR_SC','VT_FC_VR_TD')")
    public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(@PathVariable("idEmpresa") Long idEmpresa,
                                                                      FilterListDto filters,
                                                                      Pageable pageable) {
        log.info("Filters = {}", filters);
        return vtVentasService.findAllPaginateTotalizado(idDataService.getIdData(), idEmpresa, filters,
                auditorAware.getTipoPermisoFacturaVer(), auditorAware.getCurrentAuditor().orElse("SYSTEM"), pageable);
    }
}
