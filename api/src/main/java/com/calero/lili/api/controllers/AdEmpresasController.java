package com.calero.lili.api.controllers;

import com.calero.lili.core.dtos.*;
import com.calero.lili.core.services.AdEmpresasServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class AdEmpresasController {

    private final AdEmpresasServiceImpl adEmpresasService;

    @GetMapping("/{idData}/{idEmpresa}")
    public ResponseEntity<AdEmpresaGetOneDto> findById(@PathVariable Long idData, @PathVariable Long idEmpresa) {
        return ResponseEntity.ok(adEmpresasService.findById(idData, idEmpresa));
    }

    @GetMapping("/{idData}")
    public ResponseEntity<PaginatedDto<AdEmpresaGetListDto>> findAll(
            @PathVariable Long idData,
            AdEmpresaListFilterDto filters,
            Pageable pageable) {
        return ResponseEntity.ok(adEmpresasService.findAllPaginate(idData, filters, pageable));
    }

    @PostMapping("/{idData}")
    public ResponseEntity<AdEmpresaCreationResponseDto> create(
            @PathVariable Long idData,
            @RequestBody AdEmpresaRequestDto request) {
        return ResponseEntity.ok(adEmpresasService.create(idData, request));
    }

    @PutMapping("/{idData}/{idEmpresa}")
    public ResponseEntity<AdEmpresaCreationResponseDto> update(
            @PathVariable Long idData,
            @PathVariable Long idEmpresa,
            @RequestBody AdEmpresaRequestDto request) {
        return ResponseEntity.ok(adEmpresasService.update(idData, idEmpresa, request));
    }

    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<AdEmpresaRucResponseDto> findByRuc(@PathVariable String ruc) {
        return ResponseEntity.ok(adEmpresasService.findByRuc(ruc));
    }
}
