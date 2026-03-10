package com.calero.lili.api.modVentasRetenciones;

import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modVentasRetenciones.dto.CreationVentasRetencionesRequestDto;
import com.calero.lili.api.modVentasRetenciones.dto.FilterListDto;
import com.calero.lili.api.modVentasRetenciones.dto.GetDto;
import com.calero.lili.api.modVentasRetenciones.dto.GetListDto;
import com.calero.lili.api.modVentasRetenciones.dto.GetListDtoTotalizado;
import com.calero.lili.api.utils.IdDataServiceImpl;
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
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/ventas/comprobantes-retencion")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class VentasRetencionesController {

    private final VentasRetencionesServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_RT_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody CreationVentasRetencionesRequestDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_RT_MO')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idVenta") UUID idVenta,
                              @RequestBody CreationVentasRetencionesRequestDto request) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idVenta, request);
    }

    @DeleteMapping("{idEmpresa}/{idVenta}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('VT_RT_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idVenta") UUID idVenta) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idVenta);
    }

    @GetMapping("{idEmpresa}/{idVenta}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_RT_VR')")
    public GetDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                           @PathVariable("idVenta") UUID idVenta) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idVenta);
    }


    @GetMapping("reportes/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_RT_VR')")
    public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(@PathVariable("idEmpresa") Long idEmpresa,
                                                                      FilterListDto filters,
                                                                      Pageable pageable) {
        log.info("Filters = {}", filters);
        return vtVentasService.findAllPaginateTotalizado(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

    @GetMapping("excel/{idEmpresa}")
    @PreAuthorize("hasAuthority('VT_RT_EX')")
    public void exportarExcel(HttpServletResponse response,
                              @PathVariable("idEmpresa") Long idEmpresa,
                              FilterListDto filter) throws IOException {
        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        vtVentasService.exportarExcel(idDataService.getIdData(), idEmpresa, response, filter);
    }

    @GetMapping("pdf/{idEmpresa}")
    @PreAuthorize("hasAuthority('VT_RT_EX')")
    public void exportarPDF(HttpServletResponse response,
                            @PathVariable("idEmpresa") Long idEmpresa,
                            FilterListDto filters) throws DocumentException, IOException {
        vtVentasService.exportarPDF(idDataService.getIdData(), idEmpresa, response, filters);
    }
}
