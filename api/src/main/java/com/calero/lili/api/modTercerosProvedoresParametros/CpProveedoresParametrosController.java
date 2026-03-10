package com.calero.lili.api.modTercerosProvedoresParametros;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.api.modTercerosProvedoresParametros.dto.CpProveedorParametroCreationRequestDto;
import com.calero.lili.api.modTercerosProvedoresParametros.dto.CpProveedorParametroCreationResponseDto;
import com.calero.lili.api.modTercerosProvedoresParametros.dto.CpProveedorParametroListFilterDto;
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
@RequestMapping("api/v1.0/proveedores/parametros")
@CrossOrigin(originPatterns = "*")

public class CpProveedoresParametrosController {

    private final CpProveedoresParametrosServiceImpl cpProveedoresParametrosService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CP_PP_CR')")
    public CpProveedorParametroCreationResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                                          @RequestBody CpProveedorParametroCreationRequestDto request) {
        return cpProveedoresParametrosService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_PP_MO')")
    public CpProveedorParametroCreationResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                                          @PathVariable("id") UUID id,
                                                          @RequestBody CpProveedorParametroCreationRequestDto request) {
        return cpProveedoresParametrosService.update(idDataService.getIdData(), idEmpresa, id, request);
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CP_PP_EL')")
    public void delete(@PathVariable("idData") Long idData, @PathVariable("idEmpresa") Long idEmpresa, @PathVariable("id") UUID id) {
        cpProveedoresParametrosService.delete(idData, idEmpresa, id);
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_PP_VR')")
    public CpProveedorParametroCreationResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                                            @PathVariable("id") UUID id) {
        return cpProveedoresParametrosService.findFirstById(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("{idEmp}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CP_PP_VR')")
    public PaginatedDto<CpProveedorParametroCreationResponseDto> findAllPaginate(@PathVariable("idEmp") Long idEmpresa,
                                                                                 CpProveedorParametroListFilterDto filters,
                                                                                 Pageable pageable) {

        return cpProveedoresParametrosService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

}
