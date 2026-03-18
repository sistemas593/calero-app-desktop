package com.calero.lili.api.controllers;

import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.core.tablas.tbSustentos.TbSustentosCreationRequestDto;
import com.calero.lili.core.tablas.tbSustentos.TbSustentosGetListDto;
import com.calero.lili.core.tablas.tbSustentos.TbSustentosGetOneDto;
import com.calero.lili.core.tablas.tbSustentos.TbSustentosServiceImpl;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/listas-crud/sustentos")
@CrossOrigin(originPatterns = "*")

public class TbSustentosCrudController {

    private final TbSustentosServiceImpl tbService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto create(
            @RequestBody TbSustentosCreationRequestDto request) {
        return tbService.create(request);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto update(
            @PathVariable("id") String id,
            @RequestBody TbSustentosCreationRequestDto request) {
        return tbService.update(id, request);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("id") String id) {
        tbService.delete(id);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public TbSustentosGetOneDto findById(
            @PathVariable("id") String id) {
        return tbService.findById(id);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<TbSustentosGetListDto> findAllPaginate(
            FilterDto filters,
            Pageable pageable) {
        return tbService.findAllPaginate(filters, pageable);
    }

}
