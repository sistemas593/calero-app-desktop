package com.calero.lili.api.modCxP.XpFacturas;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modCxP.XpFacturas.dto.FilterXpFacturaDto;
import com.calero.lili.api.modCxP.XpFacturas.dto.XpFacturasRequestDto;
import com.calero.lili.api.modCxP.XpFacturas.dto.XpFacturasResponseDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
@RequestMapping(value = "api/v1.0/cxp-facturas")
@RequiredArgsConstructor
public class XpFacturasController {


    private final XpFacturaServiceImpl xpFacturaService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;


    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody XpFacturasRequestDto request) {
        return xpFacturaService.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{idFactura}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idFactura") UUID idFactura,
                              @Valid @RequestBody XpFacturasRequestDto request) {
        return xpFacturaService.update(idDataService.getIdData(), idEmpresa, idFactura, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("delete/{idFactura}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("idFactura") UUID idFactura) {
        xpFacturaService.delete(idFactura, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("findById/{idFactura}")
    @ResponseStatus(code = HttpStatus.OK)
    public XpFacturasResponseDto findById(@PathVariable("idFactura") UUID idFactura) {
        return xpFacturaService.findById(idFactura);
    }


    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    public PaginatedDto<XpFacturasResponseDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                               FilterXpFacturaDto filters,
                                                               Pageable pageable) {
        return xpFacturaService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }
}
