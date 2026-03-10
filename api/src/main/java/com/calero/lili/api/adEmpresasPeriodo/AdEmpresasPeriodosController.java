package com.calero.lili.api.adEmpresasPeriodo;

import com.calero.lili.api.adEmpresasPeriodo.dto.AdEmpresaPeriodoCreationRequestDto;
import com.calero.lili.api.adEmpresasPeriodo.dto.AdEmpresaPeriodoCreationResponseDto;
import com.calero.lili.api.adEmpresasPeriodo.dto.AdEmpresaPeriodoListFilterDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.dtos.PaginatedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/periodos")
@CrossOrigin(originPatterns = "*")
public class AdEmpresasPeriodosController {

    private final AdEmpresasPeriodosServiceImpl adEmpresasPeriodosService;
    private final IdDataServiceImpl idDataService;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    public AdEmpresaPeriodoCreationResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                                      @RequestBody AdEmpresaPeriodoCreationRequestDto request) {


        return adEmpresasPeriodosService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{idPeriodo}")
    @ResponseStatus(HttpStatus.OK)
    public AdEmpresaPeriodoCreationResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                                      @PathVariable("idPeriodo") UUID idPeriodo,
                                                      @RequestBody AdEmpresaPeriodoCreationRequestDto request) {


        return adEmpresasPeriodosService.update(idDataService.getIdData(), idEmpresa, idPeriodo, request);
    }

    @DeleteMapping("{idEmpresa}/{idPeriodo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEmpresa") Long idEmpresa, @PathVariable("idPeriodo") UUID idPeriodo) {


        adEmpresasPeriodosService.delete(idDataService.getIdData(), idEmpresa, idPeriodo);
    }

    @GetMapping("{idPeriodo}")
    @ResponseStatus(HttpStatus.OK)
    public AdEmpresaPeriodoCreationResponseDto findById(@PathVariable("id") UUID idPeriodo) {
        return adEmpresasPeriodosService.findFirstById(idPeriodo);
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<AdEmpresaPeriodoCreationResponseDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                                             AdEmpresaPeriodoListFilterDto filters,
                                                                             Pageable pageable) {


        return adEmpresasPeriodosService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

}
