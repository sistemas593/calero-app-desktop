package com.calero.lili.api.modComprasItems;

import com.calero.lili.api.modComprasItems.dto.GeItemGetListDto;
import com.calero.lili.api.modComprasItems.dto.GeItemGetOneDto;
import com.calero.lili.api.modComprasItems.dto.GeItemListFilterDto;
import com.calero.lili.api.modComprasItems.dto.GeItemRequestDto;
import com.calero.lili.api.modComprasItems.dto.GeItemRequestListDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.errors.ListCreationResponseDto;
import jakarta.validation.Valid;
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
@RequestMapping("api/v1.0/items")
@CrossOrigin(originPatterns = "*")

public class GeItemsController {

    private final GeItemsServiceImpl geItemsService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GE_IT_CR')")
    public GeItemGetListDto create(@Valid @PathVariable("idEmpresa") Long idEmpresa,
                                   @Valid @RequestBody GeItemRequestDto request) {
        return geItemsService.create(idDataService.getIdData(), idEmpresa, request,auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PostMapping("/createList/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GE_IT_CR')")
    public ListCreationResponseDto createList(@PathVariable("idEmpresa") Long idEmpresa,
                                              @Valid @RequestBody GeItemRequestListDto request) {
        return geItemsService.createListItems(idDataService.getIdData(), idEmpresa, request,auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_IT_MO')")
    public GeItemGetListDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                   @PathVariable("id") UUID id,
                                   @Valid @RequestBody GeItemRequestDto request) {
        return geItemsService.update(idDataService.getIdData(), idEmpresa, id, request,auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GE_IT_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {
        geItemsService.delete(idDataService.getIdData(), idEmpresa, id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_IT_VR')")
    public GeItemGetOneDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                    @PathVariable("id") UUID id) {
        return geItemsService.findById(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("listar/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_IT_VR')")
    public PaginatedDto<GeItemGetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                          GeItemListFilterDto filters,
                                                          Pageable pageable) {
        return geItemsService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

}
