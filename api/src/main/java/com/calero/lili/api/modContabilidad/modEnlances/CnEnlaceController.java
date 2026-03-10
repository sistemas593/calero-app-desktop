package com.calero.lili.api.modContabilidad.modEnlances;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.api.modContabilidad.modEnlances.dto.CnEnlaceRequestDto;
import com.calero.lili.api.modContabilidad.modEnlances.dto.CnEnlaceResponseDto;
import com.calero.lili.api.modContabilidad.modEnlances.dto.EnlaceFilterDto;
import jakarta.validation.Valid;
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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/enlaces-generales")
@CrossOrigin(originPatterns = "*")
public class CnEnlaceController {

    private final CnEnlaceServiceImpl cnEnlaceService;


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CnEnlaceResponseDto create(@RequestBody @Valid CnEnlaceRequestDto request) {
        return cnEnlaceService.create(request);
    }

    @PutMapping("{idEnlace}")
    @ResponseStatus(HttpStatus.OK)
    public CnEnlaceResponseDto update(@PathVariable("idEnlace") UUID idEnlace,
                                      @RequestBody @Valid CnEnlaceRequestDto request) {

        return cnEnlaceService.update(idEnlace, request);
    }

    @GetMapping("{idEnlace}")
    @ResponseStatus(HttpStatus.OK)
    public CnEnlaceResponseDto findById(@PathVariable("idEnlace") UUID idEnlace) {
        return cnEnlaceService.findById(idEnlace);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<CnEnlaceResponseDto> findAllPaginate(EnlaceFilterDto filters,
                                                             Pageable pageable) {
        return cnEnlaceService.findAllPaginate(filters, pageable);
    }


    @DeleteMapping("{idEnlace}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEnlace") UUID idEnlace) {
        cnEnlaceService.delete(idEnlace);
    }

}
