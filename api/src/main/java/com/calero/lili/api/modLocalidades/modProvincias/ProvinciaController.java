package com.calero.lili.api.modLocalidades.modProvincias;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.api.modLocalidades.modProvincias.dto.ProvinceListFiltersDto;
import com.calero.lili.api.modLocalidades.modProvincias.dto.RequestProvinciaDto;
import com.calero.lili.api.modLocalidades.modProvincias.dto.ResponseProvinciaDto;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("api/v1.0/provincias")
@CrossOrigin(originPatterns = "*")
public class ProvinciaController {

    private final ProvinciaServiceImpl provinciaService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseProvinciaDto create(
            @RequestBody RequestProvinciaDto request) {
        return provinciaService.create(request);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseProvinciaDto update(
            @PathVariable("id") String id,
            @RequestBody RequestProvinciaDto request) {
        return provinciaService.update(id, request);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        provinciaService.delete(id);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseProvinciaDto findById(@PathVariable("id") String id) {
        return provinciaService.findFirstById(id);
    }

    @GetMapping()
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<ResponseProvinciaDto> findAllPaginate(ProvinceListFiltersDto filters, Pageable pageable) {
        return provinciaService.findAllPaginate(filters, pageable);
    }

}
