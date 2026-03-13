package com.calero.lili.api.modComprasItemsCategorias;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.api.modComprasItemsCategorias.dto.GeItemCategoriaCreationResponseDto;
import com.calero.lili.api.modComprasItemsCategorias.dto.GeItemCategoriaReportDto;
import com.calero.lili.api.modComprasItemsCategorias.dto.GeItemMedidaListFilterDto;
import com.calero.lili.api.modComprasItemsCategorias.dto.GeItemsCategoriaCreationRequestDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
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
@RequestMapping("api/v1.0/categorias")
@CrossOrigin(originPatterns = "*")

public class GeItemsCategoriaController {

    private final GeItemsCategoriaServiceImpl geItemsCategoriaService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GE_CA_CR')")
    public GeItemCategoriaCreationResponseDto create(@RequestBody GeItemsCategoriaCreationRequestDto request) {
        return geItemsCategoriaService.create(idDataService.getIdData(), request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_CA_MO')")
    public GeItemCategoriaCreationResponseDto update(@PathVariable("id") UUID id,
                                                     @RequestBody GeItemsCategoriaCreationRequestDto request) {
        return geItemsCategoriaService.update(idDataService.getIdData(), id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GE_CA_EL')")
    public void delete(@PathVariable("id") UUID id) {
        geItemsCategoriaService.delete(idDataService.getIdData(), id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_CA_VR')")
    public GeItemCategoriaCreationResponseDto findById(@PathVariable("id") UUID id) {
        return geItemsCategoriaService.findFirstById(idDataService.getIdData(), id);
    }

    @GetMapping()
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_CA_VR')")
    public PaginatedDto<GeItemCategoriaReportDto> findAllPaginate(GeItemMedidaListFilterDto filters,
                                                                  Pageable pageable) {
        return geItemsCategoriaService.findAllPaginate(idDataService.getIdData(), filters, pageable);
    }

}
