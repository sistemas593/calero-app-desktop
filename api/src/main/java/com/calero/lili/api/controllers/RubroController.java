package com.calero.lili.api.controllers;

import com.calero.lili.core.modRRHH.modRRHHRublos.RubroServiceImpl;
import com.calero.lili.core.modRRHH.modRRHHRublos.dto.RubroRequestDto;
import com.calero.lili.core.modRRHH.modRRHHRublos.dto.RubroResponseDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import jakarta.validation.Valid;
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
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1.0/rubros")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
public class RubroController {

    private final RubroServiceImpl rubroService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;


    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public RubroResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                   @Valid @RequestBody RubroRequestDto request) {
        return rubroService.create(idDataService.getIdData(), idEmpresa, request,auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RubroResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                   @PathVariable("id") UUID id,
                                   @Valid @RequestBody RubroRequestDto request) {
        return rubroService.update(id, idDataService.getIdData(), idEmpresa, request,auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {
        rubroService.delete(id, idDataService.getIdData(), idEmpresa,auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RubroResponseDto findById(@PathVariable("id") UUID id,
                                     @PathVariable("idEmpresa") Long idEmpresa) {
        return rubroService.findById(id, idDataService.getIdData(), idEmpresa);
    }

    @GetMapping("{idEmpresa}/listar")
    @ResponseStatus(code = HttpStatus.OK)
    public List<RubroResponseDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa) {
        return rubroService.getAll(idEmpresa, idDataService.getIdData());
    }

}
