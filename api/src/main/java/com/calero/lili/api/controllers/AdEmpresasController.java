package com.calero.lili.api.controllers;


import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasServiceImpl;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaCreationResponseDto;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaGetListDto;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaGetOneDto;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaListFilterDto;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaRequestDto;
import com.calero.lili.core.modAdminEmpresas.dto.AdEmpresaRucResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/empresas")
@CrossOrigin(originPatterns = "*")

public class AdEmpresasController {


    private final AdEmpresasServiceImpl adEmpresasService;
    private final IdDataServiceImpl idDataService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('AD_EM_CR15')")
    public AdEmpresaCreationResponseDto create(@RequestBody AdEmpresaRequestDto request) {
        return adEmpresasService.create(idDataService.getIdData(), request);
    }

    @PutMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('AD_EM_MO')")
    public AdEmpresaCreationResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                               @RequestBody AdEmpresaRequestDto request) {
        return adEmpresasService.update(idDataService.getIdData(), idEmpresa, request);
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('AD_EM_VR')")
    public AdEmpresaGetOneDto findById(@PathVariable("idEmpresa") Long idEmpresa) {
        return adEmpresasService.findById(idDataService.getIdData(), idEmpresa);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('AD_EM_VR')")
    public PaginatedDto<AdEmpresaGetListDto> findAllPaginate(AdEmpresaListFilterDto filters,
                                                             Pageable pageable) {
        return adEmpresasService.findAllPaginate(idDataService.getIdData(), filters, pageable);
    }


    @GetMapping("ruc/{ruc}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('AD_EM_VR')")
    public AdEmpresaRucResponseDto findByRuc(@PathVariable("ruc") String ruc) {
        return adEmpresasService.findByRuc(ruc);
    }

}
