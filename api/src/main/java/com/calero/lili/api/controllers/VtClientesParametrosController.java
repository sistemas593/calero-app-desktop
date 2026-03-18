package com.calero.lili.api.controllers;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.modTercerosClientesParametros.VtClientesParametrosServiceImpl;
import com.calero.lili.core.modTercerosClientesParametros.dto.VtClienteParametroCreationRequestDto;
import com.calero.lili.core.modTercerosClientesParametros.dto.VtClienteParametroCreationResponseDto;
import com.calero.lili.core.modTercerosClientesParametros.dto.VtClienteParametroListFilterDto;
import com.calero.lili.core.modTercerosClientesParametros.dto.VtClienteParametroReportDto;
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
@RequestMapping("api/v1.0/clientes/parametros")
@CrossOrigin(originPatterns = "*")

public class VtClientesParametrosController {

    private final VtClientesParametrosServiceImpl vtClientesParametrosService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('VT_CP_CR')")
    public VtClienteParametroCreationResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                                        @RequestBody VtClienteParametroCreationRequestDto request) {
        return vtClientesParametrosService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_CP_MO')")
    public VtClienteParametroCreationResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                                        @PathVariable("id") UUID id,
                                                        @RequestBody VtClienteParametroCreationRequestDto request) {
        return vtClientesParametrosService.update(idDataService.getIdData(), idEmpresa, id, request);
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('VT_CP_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {

        vtClientesParametrosService.delete(idDataService.getIdData(),idEmpresa, id);
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_CP_VR')")
    public VtClienteParametroCreationResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                                          @PathVariable("id") UUID id) {
        return vtClientesParametrosService.findFirstById(idDataService.getIdData(),idEmpresa, id);
    }

    @GetMapping("{idEmp}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('VT_CP_VR')")
    public PaginatedDto<VtClienteParametroReportDto> findAllPaginate(@PathVariable("idEmp") Long idEmpresa,
                                                                     VtClienteParametroListFilterDto filters,
                                                                     Pageable pageable) {

        return vtClientesParametrosService.findAllPaginate(idDataService.getIdData(),idEmpresa, filters, pageable);
    }

}
