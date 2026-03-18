package com.calero.lili.api.controllers;

import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.tablas.tbPaises.TbPaisGetListDto;
import com.calero.lili.core.tablas.tbPaises.TbPaisesServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/listas/paises")
@CrossOrigin(originPatterns = "*")

public class TbPaisesController {

    private final TbPaisesServiceImpl tbService;

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    public List<TbPaisGetListDto> findAllPaginate(
            FilterDto filters,
            Pageable pageable) {
        return tbService.findAll(filters, pageable);
    }

}
