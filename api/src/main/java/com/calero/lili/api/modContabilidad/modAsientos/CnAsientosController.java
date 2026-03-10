package com.calero.lili.api.modContabilidad.modAsientos;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modContabilidad.modAsientos.dto.CreationAsientosRequestDto;
import com.calero.lili.api.modContabilidad.modAsientos.dto.FilterListDto;
import com.calero.lili.api.modContabilidad.modAsientos.dto.GetDto;
import com.calero.lili.api.modContabilidad.modAsientos.dto.GetListDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/contabilidad")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")

public class CnAsientosController {

    private final CnAsientosServiceImpl vtVentasService;
    private final IdDataServiceImpl idDataService;


    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CN_AS_CR')")
    public ResponseDto create(
            @PathVariable("idEmpresa") Long idEmpresa,
            @Valid @RequestBody CreationAsientosRequestDto request) {
        return vtVentasService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{idAsiento}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_MO')")
    public ResponseDto update(
            @PathVariable("idEmpresa") Long idEmpresa,
            @PathVariable("idAsiento") UUID idAsiento,
            @RequestBody CreationAsientosRequestDto request) {
        return vtVentasService.update(idDataService.getIdData(), idEmpresa, idAsiento, request);
    }

    @DeleteMapping("{idEmpresa}/{idAsiento}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CN_AS_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("idAsiento") UUID idAsiento) {
        vtVentasService.delete(idDataService.getIdData(), idEmpresa, idAsiento);
    }

    @GetMapping("{idEmpresa}/{idAsiento}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_VR')")
    public GetDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                           @PathVariable("idAsiento") UUID idAsiento) {
        return vtVentasService.findById(idDataService.getIdData(), idEmpresa, idAsiento);
    }


    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_AS_VR')")
    public PaginatedDto<GetListDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                    FilterListDto filters,
                                                    Pageable pageable) {
        return vtVentasService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }
}