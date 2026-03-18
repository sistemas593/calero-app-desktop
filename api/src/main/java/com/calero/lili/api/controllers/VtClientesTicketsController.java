package com.calero.lili.api.controllers;

import com.calero.lili.core.modClientesTickets.VtClientesTicketsServiceImpl;
import com.calero.lili.core.modClientesTickets.dto.VtClientesTicketsCreationRequestDto;
import com.calero.lili.core.modClientesTickets.dto.VtClientesTicketsGetDtoOne;
import com.calero.lili.core.modClientesTickets.dto.VtClientesTicketsGetListDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/clientes/tickets")
@CrossOrigin(originPatterns = "*")

public class VtClientesTicketsController {

    private final VtClientesTicketsServiceImpl vtClientesNovedadesService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CR_CT_CR')")
    public ResponseDto create(@RequestBody VtClientesTicketsCreationRequestDto request) {
        return vtClientesNovedadesService.create(idDataService.getIdData(), request,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idNovedad}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CR_CT_MO')")
    public ResponseDto update(@PathVariable("idNovedad") UUID idNovedad,
                              @RequestBody VtClientesTicketsCreationRequestDto request) {
        return vtClientesNovedadesService.update(idDataService.getIdData(), idNovedad, request,
                  auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idNovedad}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CR_CT_VR')")
    public VtClientesTicketsGetDtoOne findById(@PathVariable("idNovedad") UUID idNovedad) {
        return vtClientesNovedadesService.findByIdNovedad(idDataService.getIdData(), idNovedad);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CR_CT_VR')")
    public PaginatedDto<VtClientesTicketsGetListDto> findAllPaginate(FilterDto filters,
                                                                     Pageable pageable) {
        return vtClientesNovedadesService.findAllPaginate(idDataService.getIdData(), filters, pageable);
    }

}
