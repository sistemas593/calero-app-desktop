package com.calero.lili.api.modContabilidad.modCentroCostos;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.api.modContabilidad.modCentroCostos.dto.CentroCostosDtoRequest;
import com.calero.lili.api.modContabilidad.modCentroCostos.dto.CentroCostosDtoResponse;
import com.calero.lili.api.modContabilidad.modPlanCuentas.dto.CnPlanCuentaListFilterDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/centro-costos")
@CrossOrigin(originPatterns = "*")
public class CnCentroCostosController {

    private final CnCentroCostosServiceImpl cnCentroCostosService;
    private final IdDataServiceImpl idDataService;


    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CN_CC_CR')")
    public CentroCostosDtoResponse create(@PathVariable("idEmpresa") Long idEmpresa,
                                          @RequestBody @Valid CentroCostosDtoRequest request) {
        return cnCentroCostosService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_CC_MO')")
    public CentroCostosDtoResponse update(@PathVariable("idEmpresa") Long idEmpresa,
                                          @PathVariable("id") UUID id,
                                          @RequestBody @Valid CentroCostosDtoRequest request) {
        return cnCentroCostosService.update(idDataService.getIdData(), idEmpresa, id, request);
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CN_CC_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {
        cnCentroCostosService.delete(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_CC_VR')")
    public CentroCostosDtoResponse findById(@PathVariable("idEmpresa") Long idEmpresa,
                                            @PathVariable("id") UUID id) {
        return cnCentroCostosService.findById(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("listar/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_CC_VR')")
    public List<CentroCostosDtoResponse> findAll(@PathVariable("idEmpresa") Long idEmpresa) {
        return cnCentroCostosService.findAll(idDataService.getIdData(), idEmpresa);
    }

    @GetMapping("paginado/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_CC_VR')")
    public PaginatedDto<CentroCostosDtoResponse> findAll(@PathVariable("idEmpresa") Long idEmpresa,
                                                         CnPlanCuentaListFilterDto filters,
                                                         Pageable pageable) {
        return cnCentroCostosService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

}
