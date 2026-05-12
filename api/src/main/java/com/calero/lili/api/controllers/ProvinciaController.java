package com.calero.lili.api.controllers;

import com.calero.lili.core.modLocalidades.modProvincias.ProvinciaServiceImpl;
import com.calero.lili.core.modLocalidades.modProvincias.dto.ProvinceListFiltersDto;
import com.calero.lili.core.modLocalidades.modProvincias.dto.RequestProvinciaDto;
import com.calero.lili.core.modLocalidades.modProvincias.dto.ResponseProvinciaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/provincias")
@CrossOrigin(originPatterns = "*")
public class ProvinciaController {

    private final ProvinciaServiceImpl provinciaService;
    private final AuditorAware<String> auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseProvinciaDto create(
            @RequestBody RequestProvinciaDto request) {
        return provinciaService.create(request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseProvinciaDto update(
            @PathVariable("id") String id,
            @RequestBody RequestProvinciaDto request) {
        return provinciaService.update(id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        provinciaService.delete(id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseProvinciaDto findById(@PathVariable("id") String id) {
        return provinciaService.findFirstById(id);
    }

    @GetMapping()
    @ResponseStatus(code = HttpStatus.OK)
    public List<ResponseProvinciaDto> findAll(ProvinceListFiltersDto filters) {
        return provinciaService.findAll(filters);
    }

}
