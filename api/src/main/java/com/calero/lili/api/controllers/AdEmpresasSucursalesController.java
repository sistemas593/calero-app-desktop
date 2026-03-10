package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.modAdminEmpresasSucursales.AdEmpresasSucursalesServiceImpl;
import com.calero.lili.core.modAdminEmpresasSucursales.dto.AdEmpresaSucursalCreationRequestDto;
import com.calero.lili.core.modAdminEmpresasSucursales.dto.AdEmpresaSucursalGetListDto;
import com.calero.lili.core.modAdminEmpresasSucursales.dto.AdEmpresaSucursalGetOneDto;
import com.calero.lili.core.modAdminEmpresasSucursales.dto.AdEmpresaSucursalListFilterDto;
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
@RequestMapping("api/v1.0/sucursales")
@CrossOrigin(originPatterns = "*")

public class AdEmpresasSucursalesController {

    private final AdEmpresasSucursalesServiceImpl adEmpresasSucursalesService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('AD_SU_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @RequestBody AdEmpresaSucursalCreationRequestDto request) {
        return adEmpresasSucursalesService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{idSucursal}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('AD_SU_MO')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idSucursal") UUID idSucursal,
                              @RequestBody AdEmpresaSucursalCreationRequestDto request) {
        return adEmpresasSucursalesService.update(idDataService.getIdData(), idEmpresa, idSucursal, request);
    }

    @DeleteMapping("{idEmpresa}/{idSucursal}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('AD_SU_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idSucursal") UUID idSucursal) {
        adEmpresasSucursalesService.delete(idDataService.getIdData(), idEmpresa, idSucursal);
    }

    @GetMapping("{idEmpresa}/{idSucursal}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('AD_SU_VR')")
    public AdEmpresaSucursalGetOneDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                               @PathVariable("idSucursal") UUID idSucursal) {
        return adEmpresasSucursalesService.findById(idDataService.getIdData(), idEmpresa, idSucursal);
    }

    @GetMapping("listar/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('AD_SU_VR')")
    public PaginatedDto<AdEmpresaSucursalGetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                                     AdEmpresaSucursalListFilterDto filters,
                                                                     Pageable pageable) {
        return adEmpresasSucursalesService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

}
