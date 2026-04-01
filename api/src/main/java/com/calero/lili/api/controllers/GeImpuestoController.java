package com.calero.lili.api.controllers;

import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestoItemsServiceImpl;
import com.calero.lili.core.modComprasItemsImpuesto.dto.GeImpuestoRequestDto;
import com.calero.lili.core.modComprasItemsImpuesto.dto.GeImpuestoResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/items/impuestos")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class GeImpuestoController {

    private final GeImpuestoItemsServiceImpl geImpuestoService;


    @PostMapping("")
    @ResponseStatus(code = HttpStatus.CREATED)

    public GeImpuestoResponseDto create(@Valid @RequestBody GeImpuestoRequestDto request) {
        return geImpuestoService.create(request);
    }

    @PutMapping("{idImpuesto}")
    @ResponseStatus(HttpStatus.OK)
    public GeImpuestoResponseDto update(@PathVariable("idImpuesto") Long idImpuesto,
                                        @RequestBody GeImpuestoRequestDto request) {

        return geImpuestoService.update(idImpuesto, request);
    }

    @GetMapping("{idImpuesto}")
    @ResponseStatus(HttpStatus.OK)
    public GeImpuestoResponseDto findById(@PathVariable("idImpuesto") Long idImpuesto) {

        return geImpuestoService.findById(idImpuesto);
    }

    @DeleteMapping("{idImpuesto}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idImpuesto") Long idImpuesto) {
        geImpuestoService.delete(idImpuesto);
    }


    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<GeImpuestoResponseDto> findAll() {
        return geImpuestoService.findAll();
    }

}
