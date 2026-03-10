package com.calero.lili.api.modAdminEmpresasSeries;

import com.calero.lili.api.modAdminEmpresasSeries.dto.AdEmpresaSerieCreationRequestDto;
import com.calero.lili.api.modAdminEmpresasSeries.dto.AdEmpresaSerieGetDto;
import com.calero.lili.api.modAdminEmpresasSeries.dto.AdEmpresaSerieGetListDto;
import com.calero.lili.api.modAdminEmpresasSeries.dto.AdEmpresaSerieListFilterDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
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
@RequestMapping("api/v1.0/series")
@CrossOrigin(originPatterns = "*")

public class AdEmpresasSeriesController {

    private final AdEmpresasSeriesServiceImpl adEmpresasSeriesService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('AD_SE_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @RequestBody AdEmpresaSerieCreationRequestDto request) {
        return adEmpresasSeriesService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('AD_SE_MO')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("id") UUID id,
                              @RequestBody AdEmpresaSerieCreationRequestDto request) {
        System.out.println(id);
        return adEmpresasSeriesService.update(idDataService.getIdData(), idEmpresa, id, request);
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('AD_SE_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {
        adEmpresasSeriesService.delete(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("{idEmpresa}/{idSerie}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('AD_SE_VR')")
    public AdEmpresaSerieGetDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                         @PathVariable("idSerie") UUID idSerie) {
        return adEmpresasSeriesService.findById(idDataService.getIdData(), idEmpresa, idSerie);
    }

    @GetMapping("listar/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('AD_SE_VR')")
    public PaginatedDto<AdEmpresaSerieGetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                                  AdEmpresaSerieListFilterDto filters,
                                                                  Pageable pageable) {
        //log.info("Filters = {}", filters);
        return adEmpresasSeriesService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

}
