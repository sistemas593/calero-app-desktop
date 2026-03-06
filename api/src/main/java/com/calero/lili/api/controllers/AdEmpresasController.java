package com.calero.lili.api.controllers;

import com.calero.lili.core.dtos.*;
import com.calero.lili.core.services.AdEmpresasServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1.0/empresas")
@RequiredArgsConstructor
public class AdEmpresasController {

    private final AdEmpresasServiceImpl adEmpresasService;

    @GetMapping("/{idData}/{idEmpresa}")
    public ResponseEntity<AdEmpresaGetOneDto> findById(@PathVariable Long idData, @PathVariable Long idEmpresa) {
        return ResponseEntity.ok(adEmpresasService.findById(idData, idEmpresa));
    }

    @GetMapping("")
    public ResponseEntity<PaginatedDto<AdEmpresaGetListDto>> findAll(
            @PathVariable Long idData,
            AdEmpresaListFilterDto filters,
            Pageable pageable) {
        return ResponseEntity.ok(adEmpresasService.findAllPaginate(idData, filters, pageable));
    }

    @PostMapping("")
    public ResponseEntity<AdEmpresaCreationResponseDto> create(@RequestBody AdEmpresaRequestDto request) {
        return ResponseEntity.ok(adEmpresasService.create(1L, request));
    }

    @PutMapping("{idEmpresa}")
    public ResponseEntity<AdEmpresaCreationResponseDto> update(
            @PathVariable Long idEmpresa,
            @RequestBody AdEmpresaRequestDto request) {
        return ResponseEntity.ok(adEmpresasService.update(1L, idEmpresa, request));
    }

    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<AdEmpresaRucResponseDto> findByRuc(@PathVariable String ruc) {
        return ResponseEntity.ok(adEmpresasService.findByRuc(ruc));
    }
}
