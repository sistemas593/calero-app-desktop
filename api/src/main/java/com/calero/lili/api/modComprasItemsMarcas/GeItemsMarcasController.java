package com.calero.lili.api.modComprasItemsMarcas;

import com.calero.lili.api.modComprasItemsMarcas.dto.GeItemMarcasReportDto;
import com.calero.lili.api.modComprasItemsMarcas.dto.GeItemMedidaCreationResponseDto;
import com.calero.lili.api.modComprasItemsMarcas.dto.GeItemMedidaListFilterDto;
import com.calero.lili.api.modComprasItemsMarcas.dto.GeItemsMarcasCreationRequestDto;
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
@RequestMapping("api/v1.0/marcas")
@CrossOrigin(originPatterns = "*")

public class GeItemsMarcasController {

    private final GeItemsMarcasServiceImpl geItemsMedidasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GE_MA_CR')")
    public GeItemMedidaCreationResponseDto create(@RequestBody GeItemsMarcasCreationRequestDto request) {
        return geItemsMedidasService.create(idDataService.getIdData(), request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_MA_MO')")
    public GeItemMedidaCreationResponseDto update(@PathVariable("id") UUID id,
                                                  @RequestBody GeItemsMarcasCreationRequestDto request) {
        return geItemsMedidasService.update(idDataService.getIdData(), id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GE_MA_EL')")
    public void delete(@PathVariable("id") UUID id) {
        geItemsMedidasService.delete(idDataService.getIdData(), id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_MA_VR')")
    public GeItemMedidaCreationResponseDto findById(@PathVariable("id") UUID id) {
        return geItemsMedidasService.findFirstById(idDataService.getIdData(), id);
    }

    @GetMapping()
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_MA_VR')")
    public PaginatedDto<GeItemMarcasReportDto> findAllPaginate(GeItemMedidaListFilterDto filters,
                                                               Pageable pageable) {
        return geItemsMedidasService.findAllPaginate(idDataService.getIdData(), filters, pageable);
    }

}
