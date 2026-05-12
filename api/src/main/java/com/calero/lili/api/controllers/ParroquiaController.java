package com.calero.lili.api.controllers;

import com.calero.lili.core.modLocalidades.modParroquias.ParroquiaService;
import com.calero.lili.core.modLocalidades.modParroquias.dto.FilterRequestDto;
import com.calero.lili.core.modLocalidades.modParroquias.dto.ParroquiaRequestDto;
import com.calero.lili.core.modLocalidades.modParroquias.dto.ParroquiaResponseDto;
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
@RequestMapping("api/v1.0/parroquias")
@CrossOrigin(originPatterns = "*")
public class ParroquiaController {

    private final ParroquiaService parroquiaService;
    private final AuditorAware<String> auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ParroquiaResponseDto create(
            @RequestBody ParroquiaRequestDto request) {
        return parroquiaService.create(request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ParroquiaResponseDto update(
            @PathVariable("id") String id,
            @RequestBody ParroquiaRequestDto request) {
        return parroquiaService.update(id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        parroquiaService.delete(id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ParroquiaResponseDto findById(@PathVariable("id") String id) {
        return parroquiaService.findById(id);
    }

    /*@GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<ParroquiaResponseDto> findAll(FilterRequestDto filterRequest) {
        return parroquiaService.findAll(filterRequest);
    }*/
}
