package com.calero.lili.api.modVentasVendedores;

import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.api.modVentasVendedores.dto.VtVendedorCreationRequestDto;
import com.calero.lili.api.modVentasVendedores.dto.VtVendedorCreationResponseDto;
import com.calero.lili.api.modVentasVendedores.dto.VtVendedorReportDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
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

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/vendedores")
@CrossOrigin(originPatterns = "*")

public class VtVendedoresController {

    private final VtVendedoresServiceImpl vtVendedoresService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_VE_CR')")
    public VtVendedorCreationResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                                @RequestBody VtVendedorCreationRequestDto request) {
        return vtVendedoresService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_VE_MO')")
    public VtVendedorCreationResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                                @PathVariable("id") UUID id,
                                                @RequestBody VtVendedorCreationRequestDto request) {
        return vtVendedoresService.update(idDataService.getIdData(), idEmpresa, id, request);
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('VT_VE_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {
        vtVendedoresService.delete(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_VE_VR')")
    public VtVendedorCreationResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                                  @PathVariable("id") UUID id) {
        return vtVendedoresService.findById(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("listar/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_VE_VR')")
    public PaginatedDto<VtVendedorReportDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                             FilterDto filters,
                                                             Pageable pageable) {

        return vtVendedoresService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

}
