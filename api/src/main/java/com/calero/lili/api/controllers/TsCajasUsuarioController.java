package com.calero.lili.api.controllers;

import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios.TsCajasUsuarioServiceImpl;
import com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios.dto.TsCajasUsuarioRequestDto;
import com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios.dto.TsCajasUsuarioResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
@RequestMapping(value = "api/v1.0/tesoreria/cajas-usuarios")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class TsCajasUsuarioController {

    private final TsCajasUsuarioServiceImpl service;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public TsCajasUsuarioResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                            @Valid @RequestBody TsCajasUsuarioRequestDto request) {
        return service.create(idDataService.getIdData(), idEmpresa, request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idCajaUsuario}")
    @ResponseStatus(code = HttpStatus.OK)
    public TsCajasUsuarioResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                            @PathVariable("idCajaUsuario") UUID idCajaUsuario,
                                            @Valid @RequestBody TsCajasUsuarioRequestDto request) {
        return service.update(idDataService.getIdData(), idEmpresa, idCajaUsuario,
                request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{idCajaUsuario}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idCajaUsuario") UUID idCajaUsuario) {
        service.delete(idDataService.getIdData(), idEmpresa, idCajaUsuario,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{idCajaUsuario}")
    @ResponseStatus(HttpStatus.OK)
    public TsCajasUsuarioResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                              @PathVariable("idCajaUsuario") UUID idCajaUsuario) {
        return service.findById(idDataService.getIdData(), idEmpresa, idCajaUsuario);
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedDto<TsCajasUsuarioResponseDto> findAll(@PathVariable("idEmpresa") Long idEmpresa,
                                                           Pageable pageable) {
        return service.findAllPagable(idDataService.getIdData(), idEmpresa, pageable);
    }

}
