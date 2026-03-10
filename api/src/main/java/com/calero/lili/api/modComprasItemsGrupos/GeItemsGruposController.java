package com.calero.lili.api.modComprasItemsGrupos;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modComprasItemsGrupos.dto.GeItemGrupoCreationRequestDto;
import com.calero.lili.api.modComprasItemsGrupos.dto.GeItemGrupoGetListDto;
import com.calero.lili.api.modComprasItemsGrupos.dto.GeItemGrupoGetOneDto;
import com.calero.lili.api.modComprasItemsGrupos.dto.GeItemGrupoListFilterDto;
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
@RequestMapping("api/v1.0/items/grupos")
@CrossOrigin(originPatterns = "*")

public class GeItemsGruposController {

    private final GeItemsGruposServiceImpl geItemsGruposService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GE_IG_CR')")
    public ResponseDto create(
            @PathVariable("idEmpresa") Long idEmpresa,
            @RequestBody GeItemGrupoCreationRequestDto request) {
        return geItemsGruposService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_IG_MO')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("id") UUID id,
                              @RequestBody GeItemGrupoCreationRequestDto request) {
        return geItemsGruposService.update(idDataService.getIdData(), idEmpresa, id, request);
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GE_IG_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {
        geItemsGruposService.delete(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_IG_VR')")
    public GeItemGrupoGetOneDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                         @PathVariable("id") UUID id) {
        return geItemsGruposService.findFirstById(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('GE_IG_VR')")
    public PaginatedDto<GeItemGrupoGetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                               GeItemGrupoListFilterDto filters,
                                                               Pageable pageable) {
        //log.info("Filters = {}", filters);
        return geItemsGruposService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

}
