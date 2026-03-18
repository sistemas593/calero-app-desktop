package com.calero.lili.api.controllers;

import com.calero.lili.api.modAdminUsuarios.adRol.AdRolServiceImpl;
import com.calero.lili.api.modAdminUsuarios.adRol.dto.AdRolDtoRequest;
import com.calero.lili.api.modAdminUsuarios.adRol.dto.AdRolDtoResponse;
import com.calero.lili.api.modAdminUsuarios.adRol.dto.RolFilterDto;
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
@RequestMapping("api/v1.0/roles")
@CrossOrigin(originPatterns = "*")
public class AdRolController {


    private final AdRolServiceImpl adRolService;
    private final AuditorAware<String> auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('US_RL_CR')")
    public AdRolDtoResponse create(@RequestBody @Valid AdRolDtoRequest request) {
        return adRolService.create(request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idRol}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_RL_MO')")
    public AdRolDtoResponse update(@PathVariable("idRol") Long idRol,
                                   @RequestBody @Valid AdRolDtoRequest request) {

        return adRolService.update(idRol, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idRol}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_RL_VR')")
    public AdRolDtoResponse findById(@PathVariable("idRol") Long idRol) {
        return adRolService.findById(idRol);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_RL_VR')")
    public PaginatedDto<AdRolDtoResponse> findAllPaginate(RolFilterDto filters,
                                                          Pageable pageable) {
        return adRolService.findAll(filters, pageable);
    }


    @DeleteMapping("{idRol}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('US_RL_EL')")
    public void delete(@PathVariable("idRol") Long idRol) {
        adRolService.delete(idRol, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }


}
