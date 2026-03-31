package com.calero.lili.api.controllers;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.modClientesConfiguraciones.VtClientesConfiguracionesServiceImpl;
import com.calero.lili.core.modClientesConfiguraciones.dto.StEmpresasListCreationResponseDto;
import com.calero.lili.core.modClientesConfiguraciones.dto.VtClientesConfiguracionesGetListDto;
import com.calero.lili.core.modClientesConfiguraciones.dto.VtClientesConfiguracionesGetOneDto;
import com.calero.lili.core.modClientesConfiguraciones.dto.VtClientesConfiguracionesListCreationRequestDto;
import com.calero.lili.core.modClientesConfiguraciones.dto.VtClientesConfiguracionesListFilterDto;
import com.calero.lili.core.modClientesConfiguraciones.dto.VtClientesConfiguracionesRequestDto;
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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/clientes-configuraciones")
@CrossOrigin(originPatterns = "*")

public class VtClientesConfiguracionesController {

    private final VtClientesConfiguracionesServiceImpl clientesConfiguracionesService;
    private final AuditorAware<String> auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CF_CC_CR')")
    public ResponseDto create(@RequestBody @Valid VtClientesConfiguracionesRequestDto request) {
        return clientesConfiguracionesService.create( request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_CC_MO')")
    public ResponseDto update(@PathVariable("id") UUID id,
                              @RequestBody @Valid VtClientesConfiguracionesRequestDto request) {
        return clientesConfiguracionesService.update( id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CF_CC_EL')")
    public void delete(@PathVariable("id") UUID id) {
        clientesConfiguracionesService.delete(id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_CC_VR')")
    public VtClientesConfiguracionesGetOneDto findById(@PathVariable("id") UUID id) {
        return clientesConfiguracionesService.findById(id);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_CC_VR')")
    public PaginatedDto<VtClientesConfiguracionesGetListDto> findAllPaginate(
            VtClientesConfiguracionesListFilterDto filters,
            Pageable pageable) {
        return clientesConfiguracionesService.findAllPaginate(filters, pageable);
    }


    // Actualiza solo la fecha vencimiento y enviar correos, se usa para actualizar en lista
    @PostMapping("vencimiento/list")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CF_CC_MO')")
    public StEmpresasListCreationResponseDto createUpdateList(@RequestBody @Valid VtClientesConfiguracionesListCreationRequestDto request) {
        System.out.println(request);
        return clientesConfiguracionesService.createUpdateList(request);
    }

}
