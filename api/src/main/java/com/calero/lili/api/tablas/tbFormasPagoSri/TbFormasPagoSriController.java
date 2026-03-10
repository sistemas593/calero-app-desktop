package com.calero.lili.api.tablas.tbFormasPagoSri;

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
@RequestMapping("api/v1.0/listas/formas-pago-sri")
@CrossOrigin(originPatterns = "*")

public class TbFormasPagoSriController {

    private final TbFormasPagoSriServiceImpl tbService;

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<TbFormaPagoSriGetListDto> findAllPaginate(
            FilterDto filters,
            Pageable pageable) {
        return tbService.findAllPaginate(filters, pageable);
    }

}
