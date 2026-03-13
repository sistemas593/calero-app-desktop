package com.calero.lili.api.modLocalidades.modCantones;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.api.modLocalidades.modCantones.dto.RequestCantonDto;
import com.calero.lili.api.modLocalidades.modCantones.dto.ResponseCantonDto;
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
@RequestMapping("api/v1.0/cantones")
@CrossOrigin(originPatterns = "*")
public class CantonController {

    private final CantonServiceImpl cantonService;
    private final AuditorAware<String> auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCantonDto create(
            @RequestBody RequestCantonDto request) {
        return cantonService.create(request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseCantonDto update(
            @PathVariable("id") String id,
            @RequestBody RequestCantonDto request) {
        return cantonService.update(id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        cantonService.delete(id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseCantonDto findById(@PathVariable("id") String id) {
        return cantonService.findFirstById(id);
    }

    @GetMapping("paginar/{codigoProvincia}")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<ResponseCantonDto> findAllPaginate(@PathVariable("codigoProvincia") String codProvincia,
                                                           Pageable pageable) {
        return cantonService.findAllPaginate(codProvincia, pageable);
    }

}
