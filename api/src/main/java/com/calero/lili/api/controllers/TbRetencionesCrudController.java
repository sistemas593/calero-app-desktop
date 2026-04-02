package com.calero.lili.api.controllers;

import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.tablas.tbRetenciones.TbRetencionesCreationRequestDto;
import com.calero.lili.core.tablas.tbRetenciones.TbRetencionesGetListDto;
import com.calero.lili.core.tablas.tbRetenciones.TbRetencionesGetOneDto;
import com.calero.lili.core.tablas.tbRetenciones.TbRetencionesServiceImpl;
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
@RequestMapping("api/v1.0/listas-crud/retenciones")
@CrossOrigin(originPatterns = "*")

public class TbRetencionesCrudController {

    private final TbRetencionesServiceImpl tbService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public ResponseDto create(@RequestBody TbRetencionesCreationRequestDto request) {
        return tbService.create(request);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public ResponseDto update(@PathVariable("id") String id,
                              @RequestBody TbRetencionesCreationRequestDto request) {
        return tbService.update(id, request);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public void delete(@PathVariable("id") String id) {
        tbService.delete(id);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public TbRetencionesGetOneDto findById(@PathVariable("id") String id) {
        return tbService.findById(id);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<TbRetencionesGetListDto> findAllPaginate(FilterDto filters,
                                                                 Pageable pageable) {
        return tbService.findAllPaginate(filters, pageable);
    }

}
