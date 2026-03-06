package com.calero.lili.api.controllers;

import com.calero.lili.core.dtos.*;
import com.calero.lili.core.services.AdEmpresasSucursalesServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class AdEmpresasSucursalesController {

    private final AdEmpresasSucursalesServiceImpl adEmpresasSucursalesService;

    @GetMapping("/{idData}/{idEmpresa}/{idSucursal}")
    public ResponseEntity<AdEmpresaSucursalGetOneDto> findById(
            @PathVariable Long idData,
            @PathVariable Long idEmpresa,
            @PathVariable UUID idSucursal) {
        return ResponseEntity.ok(adEmpresasSucursalesService.findById(idData, idEmpresa, idSucursal));
    }

    @GetMapping("/{idData}/{idEmpresa}")
    public ResponseEntity<PaginatedDto<AdEmpresaSucursalGetListDto>> findAll(
            @PathVariable Long idData,
            @PathVariable Long idEmpresa,
            AdEmpresaSucursalListFilterDto filters,
            Pageable pageable) {
        return ResponseEntity.ok(adEmpresasSucursalesService.findAllPaginate(idData, idEmpresa, filters, pageable));
    }

    @PostMapping("/{idData}/{idEmpresa}")
    public ResponseEntity<Void> create(
            @PathVariable Long idData,
            @PathVariable Long idEmpresa,
            @RequestBody AdEmpresaSucursalCreationRequestDto request) {
        adEmpresasSucursalesService.create(idData, idEmpresa, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{idData}/{idEmpresa}/{idSucursal}")
    public ResponseEntity<Void> update(
            @PathVariable Long idData,
            @PathVariable Long idEmpresa,
            @PathVariable UUID idSucursal,
            @RequestBody AdEmpresaSucursalCreationRequestDto request) {
        adEmpresasSucursalesService.update(idData, idEmpresa, idSucursal, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idData}/{idEmpresa}/{idSucursal}")
    public ResponseEntity<Void> delete(
            @PathVariable Long idData,
            @PathVariable Long idEmpresa,
            @PathVariable UUID idSucursal) {
        adEmpresasSucursalesService.delete(idData, idEmpresa, idSucursal);
        return ResponseEntity.ok().build();
    }
}
