package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.tablas.tbFormasPagoSri.TbFormaPagoSriCreationRequestDto;
import com.calero.lili.core.tablas.tbFormasPagoSri.TbFormaPagoSriGetListDto;
import com.calero.lili.core.tablas.tbFormasPagoSri.TbFormaPagoSriGetOneDto;
import com.calero.lili.core.tablas.tbFormasPagoSri.TbFormasPagoSriServiceImpl;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/listas-crud/formas-pago-sri")
@CrossOrigin(originPatterns = "*")

public class TbFormasPagoSriCrudController {

    private final TbFormasPagoSriServiceImpl tbDocumentosService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public ResponseDto create(@RequestBody TbFormaPagoSriCreationRequestDto request) {
        return tbDocumentosService.create(request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public ResponseDto update(@PathVariable("id") String id,
                              @RequestBody TbFormaPagoSriCreationRequestDto request) {
        return tbDocumentosService.update(id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public void delete(@PathVariable("id") String id) {
        tbDocumentosService.delete(id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public TbFormaPagoSriGetOneDto findById(@PathVariable("id") String id) {
        return tbDocumentosService.findById(id);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public PaginatedDto<TbFormaPagoSriGetListDto> findAllPaginate(
            FilterDto filters,
            Pageable pageable) {
        return tbDocumentosService.findAllPaginate(filters, pageable);
    }

}
