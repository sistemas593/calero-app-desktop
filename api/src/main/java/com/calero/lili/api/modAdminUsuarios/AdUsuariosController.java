package com.calero.lili.api.modAdminUsuarios;

import com.calero.lili.api.modAdminUsuarios.dto.AdUsuarioCreationResponseDto;
import com.calero.lili.api.modAdminUsuarios.dto.AdUsuarioListFilterDto;
import com.calero.lili.api.modAdminUsuarios.dto.AdUsuarioReportDto;
import com.calero.lili.api.modAdminUsuarios.dto.AdUsuarioRequestDto;
import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.core.dtos.PaginatedDto;
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
@RequestMapping("api/v1.0/usuarios")
@CrossOrigin(originPatterns = "*")

public class AdUsuariosController {

    private final AdUsuarioServiceImpl adUsuarioService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('US_US_CR')")
    public AdUsuarioCreationResponseDto create(@RequestBody AdUsuarioRequestDto request) {
        return adUsuarioService.create(request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idUsuario}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_US_MO')")
    public AdUsuarioCreationResponseDto update(@PathVariable("idUsuario") Long idUsuario,
                                               @RequestBody AdUsuarioRequestDto request) {

        return adUsuarioService.update(idUsuario, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idUsuario}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_US_VR')")
    public AdUsuarioReportDto findById(@PathVariable("idUsuario") Long idUsuario) {
        return adUsuarioService.findByIdUsuario(idUsuario);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('US_US_VR')")
    public PaginatedDto<AdUsuarioReportDto> findAllPaginate(AdUsuarioListFilterDto filters,
                                                            Pageable pageable) {
        return adUsuarioService.findAllPaginate(filters, pageable);
    }

}
