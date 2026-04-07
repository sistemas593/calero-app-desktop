package com.calero.lili.api.controllers;

import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.TbParaisoFiscalServiceImpl;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.dto.TbParaisoFiscalRequestDto;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.dto.TbParaisoFiscalResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/listas-crud/paraisos-fiscales")
@CrossOrigin(originPatterns = "*")

public class TbParaisosFiscalesCrudController {

    private final TbParaisoFiscalServiceImpl tbParaisoFiscalService;
    private final AuditorAware<String> auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public TbParaisoFiscalResponseDto create(@RequestBody TbParaisoFiscalRequestDto request) {
        return tbParaisoFiscalService.create(auditorAware.getCurrentAuditor().orElse("SYSTEM"), request);
    }

    @PutMapping("{codigo}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public TbParaisoFiscalResponseDto update(@PathVariable("codigo") String codigo,
                                             @RequestBody TbParaisoFiscalRequestDto request) {
        return tbParaisoFiscalService.update(auditorAware.getCurrentAuditor().orElse("SYSTEM"), codigo, request);
    }

    @DeleteMapping("{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public void delete(@PathVariable("codigo") String codigo) {
        tbParaisoFiscalService.delete(auditorAware.getCurrentAuditor().orElse("SYSTEM"), codigo);
    }

    @GetMapping("{codigo}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CF_TBC_GR')")
    public TbParaisoFiscalResponseDto findById(@PathVariable("codigo") String codigo) {
        return tbParaisoFiscalService.findById(codigo);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    public List<TbParaisoFiscalResponseDto> findAllPaginate(FilterDto filters) {
        return tbParaisoFiscalService.findAll(filters);
    }

}
