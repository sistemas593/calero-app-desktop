package com.calero.lili.api.controllers;

import com.calero.lili.api.modAdminUsuarios.adGrupos.AdGruposServiceImpl;
import com.calero.lili.api.modAdminUsuarios.adGrupos.dto.AdGruposRequestDto;
import com.calero.lili.api.modAdminUsuarios.adGrupos.dto.AdGruposResponseDto;
import com.calero.lili.api.modAdminUsuarios.adGrupos.dto.GruposFilter;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/grupos")
@CrossOrigin(originPatterns = "*")
public class AdGruposController {


    private final AdGruposServiceImpl adGrupoPermisoService;
    private final AuditorAware<String> auditorAware;
    private final IdDataServiceImpl idDataService;


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('US_GR_CR')")
    public AdGruposResponseDto create(@RequestBody @Valid AdGruposRequestDto request) {
        return adGrupoPermisoService.create(idDataService.getIdData(), request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idGrupoPermiso}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_GR_MO')")
    public AdGruposResponseDto update(@PathVariable("idGrupoPermiso") Long idGrupoPermiso,
                                      @RequestBody @Valid AdGruposRequestDto request) {

        return adGrupoPermisoService.update(idDataService.getIdData(), idGrupoPermiso, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idGrupoPermiso}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_GR_VR')")
    public AdGruposResponseDto findById(@PathVariable("idGrupoPermiso") Long idGrupoPermiso) {
        return adGrupoPermisoService.findById(idDataService.getIdData(), idGrupoPermiso);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_GR_VR')")
    public PaginatedDto<AdGruposResponseDto> findAllPaginate(GruposFilter filters,
                                                             Pageable pageable) {
        return adGrupoPermisoService.findAll(idDataService.getIdData(), filters, pageable);
    }


    @DeleteMapping("{idGrupoPermiso}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('US_GR_EL')")
    public void delete(@PathVariable("idGrupoPermiso") Long idGrupoPermiso) {
        adGrupoPermisoService.delete(idDataService.getIdData(), idGrupoPermiso, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

}
