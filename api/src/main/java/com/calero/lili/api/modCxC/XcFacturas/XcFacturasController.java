package com.calero.lili.api.modCxC.XcFacturas;

import com.calero.lili.core.dtos.PaginatedDto;
import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modCxC.XcFacturas.dto.FilterXcFacturaDto;
import com.calero.lili.api.modCxC.XcFacturas.dto.RequestXcFacturasDto;
import com.calero.lili.api.modCxC.XcFacturas.dto.ResponseXcFacturasDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping(value = "api/v1.0/cxc-facturas")
@RequiredArgsConstructor
public class XcFacturasController {


    private final XcFacturaServiceImpl tsComprobanteIngresoService;
    private final IdDataServiceImpl idDataService;


    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CX_XC_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody RequestXcFacturasDto request) {
        return tsComprobanteIngresoService.create(idDataService.getIdData(), idEmpresa, request, UUID.randomUUID());
    }

    @PutMapping("{idEmpresa}/{idFactura}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CX_XC_MO')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idFactura") UUID idFactura,
                              @Valid @RequestBody RequestXcFacturasDto request) {
        return tsComprobanteIngresoService.update(idDataService.getIdData(), idEmpresa, idFactura, request);
    }

    @DeleteMapping("delete/{idFactura}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CX_XC_EL')")
    public void delete(@PathVariable("idFactura") UUID idFactura) {

        tsComprobanteIngresoService.delete(idFactura);
    }

    @GetMapping("findById/{idFactura}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CX_XC_VR')")
    public ResponseXcFacturasDto findById(@PathVariable("idFactura") UUID idFactura) {
        return tsComprobanteIngresoService.findById(idFactura);
    }


    @GetMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CX_XC_VR')")
    public PaginatedDto<ResponseXcFacturasDto> findAllPaginate(@PathVariable("idEmpresa") Long idEmpresa,
                                                               FilterXcFacturaDto filters,
                                                               Pageable pageable) {
        return tsComprobanteIngresoService.findAllPaginate(idDataService.getIdData(), idEmpresa, filters, pageable);
    }
}
