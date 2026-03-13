package com.calero.lili.api.modAdminUsuarios.adPermisos;

import com.calero.lili.api.modAdminUsuarios.adPermisos.dto.AdPermisosRequestDto;
import com.calero.lili.api.modAdminUsuarios.adPermisos.dto.AdPermisosResponseDto;
import com.calero.lili.api.modAdminUsuarios.adPermisos.dto.PermisoFilterDto;
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
@RequestMapping("api/v1.0/permisos")
@CrossOrigin(originPatterns = "*")
public class AdPermisoController {

    private final AdPermisosServiceImpl adPermisosService;
    private final AuditorAware<String> auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('US_PE_CR')")
    public AdPermisosResponseDto create(@RequestBody @Valid AdPermisosRequestDto request) {
        return adPermisosService.create(request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idPermiso}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_PE_MO')")
    public AdPermisosResponseDto update(@PathVariable("idPermiso") Long idPermiso,
                                        @RequestBody @Valid AdPermisosRequestDto request) {

        return adPermisosService.update(idPermiso, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idPermiso}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_PE_VR')")
    public AdPermisosResponseDto findById(@PathVariable("idPermiso") Long idPermiso) {
        return adPermisosService.findById(idPermiso);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_PE_VR')")
    public PaginatedDto<AdPermisosResponseDto> findAllPaginate(PermisoFilterDto filters,
                                                               Pageable pageable) {
        return adPermisosService.findAll(filters, pageable);
    }


    @DeleteMapping("{idPermiso}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('US_PE_EL')")
    public void delete(@PathVariable("idPermiso") Long idPermiso) {
        adPermisosService.delete(idPermiso, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

}
