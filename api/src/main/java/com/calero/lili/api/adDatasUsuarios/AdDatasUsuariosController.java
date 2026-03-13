package com.calero.lili.api.adDatasUsuarios;


import com.calero.lili.api.adDatasUsuarios.dto.AdDataUsuarioCreationRequestDto;
import com.calero.lili.api.adDatasUsuarios.dto.AdDataUsuarioCreationResponseDto;
import com.calero.lili.api.adDatasUsuarios.dto.AdDataUsuarioListFilterDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/data-usuarios")
@CrossOrigin(originPatterns = "*")

public class AdDatasUsuariosController {

    private final AdDatasUsuariosServiceImpl adDatasUsuariosService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("{idData}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CF_DTU_CR')")
    public AdDataUsuarioCreationResponseDto create(@PathVariable("idData") Long idData,
                                                   @RequestBody AdDataUsuarioCreationRequestDto request) {
        return adDatasUsuariosService.create(idData, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }


    @PostMapping("{idRegistro}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CF_DTU_VR')")
    public AdDataUsuarioCreationResponseDto findById(@PathVariable("idRegistro") UUID idRegistro) {
        return adDatasUsuariosService.findByIdData(idRegistro);
    }

    @GetMapping()
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_DTU_VR')")
    public PaginatedDto<AdDataUsuarioCreationResponseDto> findAllPaginate(AdDataUsuarioListFilterDto filters,
                                                                          Pageable pageable) {
        return adDatasUsuariosService.findAllPaginate(filters, pageable);
    }

}
