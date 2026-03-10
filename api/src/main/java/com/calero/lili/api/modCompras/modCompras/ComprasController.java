package com.calero.lili.api.modCompras.modCompras;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modCompras.modCompras.dto.CompraRequestDto;
import com.calero.lili.api.modCompras.modCompras.dto.FilterListDto;
import com.calero.lili.api.modCompras.modCompras.dto.GetDto;
import com.calero.lili.api.modCompras.modCompras.dto.GetListDto;
import com.calero.lili.api.modCompras.modCompras.dto.GetListDtoTotalizado;
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
@RequestMapping(value = "api/v1.0/compras")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class ComprasController {

    private final ComprasServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CP_CP_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody CompraRequestDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{idCompra}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_CP_MO')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idCompra") UUID idCompra,
                              @RequestBody CompraRequestDto request) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idCompra, request);
    }

    @DeleteMapping("{idEmpresa}/{idCompra}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CP_CP_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa, @PathVariable("idCompra") UUID idCompra) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idCompra);
    }

    @GetMapping("{idEmpresa}/{idCompra}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_CP_VR')")
    public GetDto findById(@PathVariable("idEmpresa") Long idEmpresa, @PathVariable("idCompra") UUID idCompra) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idCompra);
    }


    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_CP_VR')")
    public PaginatedDto<GetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                    FilterListDto filters,
                                                    Pageable pageable) {
        return vtVentasService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

    @GetMapping("reportes/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_CP_VR')")
    public GetListDtoTotalizado<GetListDto> findAllPaginateTotalizado(
            @PathVariable("idEmpresa") Long idEmpresa,
            FilterListDto filters,
            Pageable pageable) {
        log.info("Filters = {}", filters);
        return vtVentasService.findAllPaginateTotalizado(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

    @GetMapping("excel/{idEmpresa}")
    @PreAuthorize("hasAuthority('CP_CP_EX')")
    public void exportarExcel(HttpServletResponse response,
                              @PathVariable("idEmpresa") Long idEmpresa,
                              FilterListDto filter) throws IOException {
        log.info("Iniciando la exportación a Excel con el filtro: {}", filter);
        vtVentasService.exportarExcel(idDataService.getIdData(), idEmpresa, response, filter);
    }

    @GetMapping("pdf/{idEmpresa}")
    @PreAuthorize("hasAuthority('CP_CP_MO')")
    public void exportarPDF(HttpServletResponse response,
                            @PathVariable("idEmpresa") Long idEmpresa,
                            FilterListDto filters) throws DocumentException, IOException {
        vtVentasService.exportarPDF(idDataService.getIdData(), idEmpresa, response, filters);
    }

}
