package com.calero.lili.api.modTesoreria.modTesoreriaCierresCajas;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.api.modTesoreria.modTesoreriaCierresCajas.dto.BcCajaTpvCreationRequestDto;
import com.calero.lili.api.modTesoreria.modTesoreriaCierresCajas.dto.BcCajaTpvCreationResponseDto;
import com.calero.lili.api.modTesoreria.modTesoreriaCierresCajas.dto.BcCajaTpvListFilterDto;
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
@RequestMapping("api/v1.0/cajas/tpv")
@CrossOrigin(originPatterns = "*")

public class BcCajasTpvController {

    private final TsCajasTpvServiceImpl bcCajasTpvService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    public BcCajaTpvCreationResponseDto create(
            @PathVariable("idEmpresa") Long idEmpresa,
            @RequestBody BcCajaTpvCreationRequestDto request) {
        return bcCajasTpvService.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BcCajaTpvCreationResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                               @PathVariable("id") UUID id,
                                               @RequestBody BcCajaTpvCreationRequestDto request) {
        return bcCajasTpvService.update(idDataService.getIdData(),idEmpresa, id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") Long id) {
        bcCajasTpvService.delete(idDataService.getIdData(),idEmpresa, id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BcCajaTpvCreationResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                                 @PathVariable("id") UUID id) {
        return bcCajasTpvService.findFirstById(idDataService.getIdData(),idEmpresa, id);
    }

    @GetMapping("{idEmp}")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<BcCajaTpvCreationResponseDto> findAllPaginate(@PathVariable("idEmp") Long idEmpresa,
                                                                      BcCajaTpvListFilterDto filters,
                                                                      Pageable pageable) {

        return bcCajasTpvService.findAllPaginate(idDataService.getIdData(),idEmpresa, filters, pageable);
    }

}
