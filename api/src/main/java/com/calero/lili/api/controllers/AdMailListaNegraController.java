package com.calero.lili.api.controllers;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.modAdminlistaNegra.AdMailListNegraServiceImpl;
import com.calero.lili.core.modAdminlistaNegra.dto.FilterMailBlackDto;
import com.calero.lili.core.modAdminlistaNegra.dto.MailBlackResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1.0/lista-negra")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class AdMailListaNegraController {

    private final AdMailListNegraServiceImpl adMailListNegraService;

    @GetMapping("getEmail")
    @ResponseStatus(HttpStatus.OK)
    public MailBlackResponseDto findById(FilterMailBlackDto model) {
        return adMailListNegraService.findByEmail(model);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<MailBlackResponseDto> findAllPaginate(FilterMailBlackDto model, Pageable pageable) {
        return adMailListNegraService.getAll(model, pageable);
    }

    @DeleteMapping("delete")
    @ResponseStatus(code = HttpStatus.OK)
    public void delete(FilterMailBlackDto model) {
        adMailListNegraService.delete(model);
    }


}
