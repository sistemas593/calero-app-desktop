package com.calero.lili.api.controllers;


import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.modAdModulos.AdModuloServiceImpl;
import com.calero.lili.core.modAdModulos.dto.AdModuloRequestDto;
import com.calero.lili.core.modAdModulos.dto.AdModuloResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
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
@RequestMapping("api/v1.0/modulos")
@CrossOrigin(originPatterns = "*")
public class AdModuloController {

    private final AdModuloServiceImpl adModuloService;
    private final AuditorAware<String> auditorAware;


    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public AdModuloResponseDto create(@RequestBody @Valid AdModuloRequestDto request) {
        return adModuloService.create(request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idModulo}")
    @ResponseStatus(HttpStatus.OK)
    public AdModuloResponseDto update(@PathVariable("idModulo") Long idModulo,
                                      @RequestBody @Valid AdModuloRequestDto request) {
        return adModuloService.update(idModulo, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idModulo}")
    @ResponseStatus(HttpStatus.OK)
    public AdModuloResponseDto findById(@PathVariable("idModulo") Long idModulo) {
        return adModuloService.findById(idModulo);
    }


    @DeleteMapping("{idModulo}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("idModulo") Long idModulo) {
        adModuloService.delete(idModulo, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedDto<AdModuloResponseDto> findAllPaginate(Pageable pageable) {
        return adModuloService.findAllPaginate(pageable);
    }

}
