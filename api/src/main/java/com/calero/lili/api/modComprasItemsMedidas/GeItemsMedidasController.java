package com.calero.lili.api.modComprasItemsMedidas;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.api.modComprasItemsMedidas.dto.GeItemMedidaCreationRequestDto;
import com.calero.lili.api.modComprasItemsMedidas.dto.GeItemMedidaCreationResponseDto;
import com.calero.lili.api.modComprasItemsMedidas.dto.GeItemMedidaListFilterDto;
import com.calero.lili.api.modComprasItemsMedidas.dto.GeItemMedidaReportDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("api/v1.0/unidades-medida")
@CrossOrigin(originPatterns = "*")

public class GeItemsMedidasController {

    private final GeItemsMedidasServiceImpl geItemsMedidasService;
    private final IdDataServiceImpl idDataService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GE_ME_CR')")
    public GeItemMedidaCreationResponseDto create(@RequestBody GeItemMedidaCreationRequestDto request) {
        return geItemsMedidasService.create(idDataService.getIdData(), request);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_ME_MO')")
    public GeItemMedidaCreationResponseDto update(@PathVariable("id") UUID id,
                                                  @RequestBody GeItemMedidaCreationRequestDto request) {
        return geItemsMedidasService.update(idDataService.getIdData(), id, request);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GE_ME_EL')")
    public void delete(@PathVariable("id") UUID id) {
        geItemsMedidasService.delete(idDataService.getIdData(), id);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_ME_VR')")
    public GeItemMedidaCreationResponseDto findById(@PathVariable("id") UUID id) {
        return geItemsMedidasService.findFirstById(idDataService.getIdData(), id);
    }

    @GetMapping()
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_ME_VR')")
    public PaginatedDto<GeItemMedidaReportDto> findAllPaginate(
            GeItemMedidaListFilterDto filters,
            Pageable pageable) {
        //log.info("Filters = {}", filters);
        return geItemsMedidasService.findAllPaginate(idDataService.getIdData(), filters, pageable);
    }

}
