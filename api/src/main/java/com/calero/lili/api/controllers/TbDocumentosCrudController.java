package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.tablas.tbDocumentos.TbDocumentosCreationRequestDto;
import com.calero.lili.core.tablas.tbDocumentos.TbDocumentosGetListDto;
import com.calero.lili.core.tablas.tbDocumentos.TbDocumentosGetOneDto;
import com.calero.lili.core.tablas.tbDocumentos.TbDocumentosServiceImpl;
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
@RequestMapping("api/v1.0/listas-crud/documentos")
@CrossOrigin(originPatterns = "*")

public class TbDocumentosCrudController {

    private final TbDocumentosServiceImpl tbService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public ResponseDto create(@RequestBody TbDocumentosCreationRequestDto request) {
        return tbService.create(request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public ResponseDto update(@PathVariable("id") String id,
                              @RequestBody TbDocumentosCreationRequestDto request) {
        return tbService.update(id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public void delete(@PathVariable("id") String id) {
        tbService.delete(id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public TbDocumentosGetOneDto findById(@PathVariable("id") String id) {
        return tbService.findById(id);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public PaginatedDto<TbDocumentosGetListDto> findAllPaginate(FilterDto filters,
                                                                Pageable pageable) {
        return tbService.findAllPaginate(filters, pageable);
    }

}
