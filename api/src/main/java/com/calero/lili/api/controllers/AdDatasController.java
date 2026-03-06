package com.calero.lili.api.controllers;

import com.calero.lili.core.dtos.AdDatasDto;
import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.services.AdDatasServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/datas")
@RequiredArgsConstructor
public class AdDatasController {

    private final AdDatasServiceImpl adDatasService;

    @GetMapping("/{idData}")
    public ResponseEntity<AdDatasDto> findById(@PathVariable Long idData) {
        return ResponseEntity.ok(adDatasService.findByIdData(idData));
    }

    @GetMapping
    public ResponseEntity<PaginatedDto<AdDatasDto>> findAll(FilterDto filters, Pageable pageable) {
        return ResponseEntity.ok(adDatasService.findAllPaginate(filters, pageable));
    }
}
