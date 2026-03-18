package com.calero.lili.api.controllers;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.TsBancosMovimientosServiceImpl;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoCreationRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoCreationResponseDto;
import com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos.dto.BcBancoMovimientoListFilterDto;
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
@RequestMapping("api/v1.0/bancos/movimientos")
@CrossOrigin(originPatterns = "*")

public class BcBancosMovimientosController {

    private final TsBancosMovimientosServiceImpl bcBancosMovimientosService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    public BcBancoMovimientoCreationResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                                                       @RequestBody BcBancoMovimientoCreationRequestDto request) {
        return bcBancosMovimientosService.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BcBancoMovimientoCreationResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                                                       @PathVariable("id") UUID id,
                                                       @RequestBody BcBancoMovimientoCreationRequestDto request) {
        return bcBancosMovimientosService.update(idDataService.getIdData(), idEmpresa, id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {
        bcBancosMovimientosService.delete(idDataService.getIdData(), idEmpresa, id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BcBancoMovimientoCreationResponseDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                                         @PathVariable("id") UUID id) {
        return bcBancosMovimientosService.findById(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<BcBancoMovimientoCreationResponseDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                                              BcBancoMovimientoListFilterDto filters,
                                                                              Pageable pageable) {
        //log.info("Filters = {}", filters);
        return bcBancosMovimientosService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }

}
