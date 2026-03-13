package com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones.dto.BcBancoConciliacionCreationRequestDto;
import com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones.dto.BcBancoConciliacionCreationResponseDto;
import com.calero.lili.api.modTesoreria.modTesoreriaBancosConcilaciones.dto.BcBancoConciliacionListFilterDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
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
@RequestMapping("api/v1.0/conciliaciones")
@CrossOrigin(originPatterns = "*")

public class BcBancosConciliacionesController {

    private final TsBancosConciliacionesServiceImpl bcBancosConciliacionesService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    public BcBancoConciliacionCreationResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                                         @RequestBody BcBancoConciliacionCreationRequestDto request) {
        return bcBancosConciliacionesService.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BcBancoConciliacionCreationResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                                         @PathVariable("id") UUID id,
                                                         @RequestBody BcBancoConciliacionCreationRequestDto request) {

        return bcBancosConciliacionesService.update(idDataService.getIdData(), idEmpresa, id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {

        bcBancosConciliacionesService.delete(idDataService.getIdData(), idEmpresa, id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BcBancoConciliacionCreationResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                                           @PathVariable("id") UUID id) {

        return bcBancosConciliacionesService.findFirstById(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("{idEmp}")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<BcBancoConciliacionCreationResponseDto> findAllPaginate(@PathVariable("idEmp") Long idEmpresa,
                                                                                BcBancoConciliacionListFilterDto filters,
                                                                                Pageable pageable) {

        //log.info("Filters = {}", filters);
        return bcBancosConciliacionesService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

}
