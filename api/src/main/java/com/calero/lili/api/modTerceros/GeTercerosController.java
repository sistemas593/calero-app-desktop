package com.calero.lili.api.modTerceros;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.api.modTerceros.dto.GeTerceroFilterDto;
import com.calero.lili.api.modTerceros.dto.GeTerceroGetListDto;
import com.calero.lili.api.modTerceros.dto.GeTerceroGetOneDto;
import com.calero.lili.api.modTerceros.dto.GeTerceroRequestDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/terceros")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class GeTercerosController {

    private final GeTercerosServiceImpl vtClientesService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GE_TE_CR')")
    public GeTerceroGetListDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                      @Valid @RequestBody GeTerceroRequestDto request) {
        return vtClientesService.create(idEmpresa, idDataService.getIdData(), request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_TE_MO')")
    public GeTerceroGetListDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                      @PathVariable("id") UUID id,
                                      @Valid @RequestBody GeTerceroRequestDto request) {
        return vtClientesService.update(idEmpresa, idDataService.getIdData(), id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GE_TE_EL')")
    public void delete(@PathVariable("id") UUID id) {
        vtClientesService.delete(idDataService.getIdData(), id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_TE_VR')")
    public GeTerceroGetOneDto findById(@PathVariable("id") UUID id,
                                       @PathVariable("idEmpresa") Long idEmpresa) {
        return vtClientesService.findById(idDataService.getIdData(), id, idEmpresa);
    }

    @GetMapping("listar")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_TE_VR')")
    public PaginatedDto<GeTerceroGetListDto> findAllPaginate(GeTerceroFilterDto filters,
                                                             Pageable pageable) {
        return vtClientesService.findAllPaginate(idDataService.getIdData(), filters, pageable);
    }

}
