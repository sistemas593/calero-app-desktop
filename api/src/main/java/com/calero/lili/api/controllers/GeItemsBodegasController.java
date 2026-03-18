package com.calero.lili.api.controllers;

import com.calero.lili.core.modComprasItemsBodegas.GeItemsBodegasServiceImpl;
import com.calero.lili.core.modComprasItemsBodegas.dto.GeItemBodegaCreationRequestDto;
import com.calero.lili.core.modComprasItemsBodegas.dto.GeItemBodegaCreationResponseDto;
import com.calero.lili.core.modComprasItemsBodegas.dto.GeItemBodegaListFilterDto;
import com.calero.lili.core.modComprasItemsBodegas.dto.GeItemBodegaReportDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import lombok.RequiredArgsConstructor;
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


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/bodegas")
@CrossOrigin(originPatterns = "*")

public class GeItemsBodegasController {

    private final GeItemsBodegasServiceImpl geItemsMedidasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GE_BO_CR')")
    public GeItemBodegaCreationResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                                  @RequestBody GeItemBodegaCreationRequestDto request) {
        return geItemsMedidasService.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_BO_MO')")
    public GeItemBodegaCreationResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                                  @PathVariable("id") UUID id,
                                                  @RequestBody GeItemBodegaCreationRequestDto request) {
        return geItemsMedidasService.update(idDataService.getIdData(), idEmpresa, id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GE_BO_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {
        geItemsMedidasService.delete(idDataService.getIdData(), idEmpresa, id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_BO_VR')")
    public GeItemBodegaCreationResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                                    @PathVariable("id") UUID id) {
        return geItemsMedidasService.findFirstById(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("listar/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_BO_VR')")
    public PaginatedDto<GeItemBodegaReportDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                               GeItemBodegaListFilterDto filters,
                                                               Pageable pageable) {
        //log.info("Filters = {}", filters);
        return geItemsMedidasService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

}
