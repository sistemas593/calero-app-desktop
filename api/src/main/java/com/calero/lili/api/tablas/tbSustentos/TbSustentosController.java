package com.calero.lili.api.tablas.tbSustentos;

import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/listas/sustentos")
@CrossOrigin(originPatterns = "*")

public class TbSustentosController {

    private final TbSustentosServiceImpl tbService;

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<TbSustentosGetListDto> findAllPaginate(
            FilterDto filters,
            Pageable pageable) {
        return tbService.findAllPaginate(filters, pageable);
    }

}
