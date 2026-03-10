package com.calero.lili.api.modCxC.XcPagos;

import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modCxC.XcPagos.dto.RequestPagoDto;
import com.calero.lili.api.modCxC.XcPagos.dto.ResponseXcPagosDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1.0/cxc-pagos")
@RequiredArgsConstructor
public class XcPagosController {


    private final XcPagoServiceImpl xcPagoService;
    private final IdDataServiceImpl idDataService;


    @PostMapping("{idEmpresa}")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CX_XP_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @Valid @RequestBody RequestPagoDto request) {
        return xcPagoService.create(idDataService.getIdData(), idEmpresa, request);
    }

    @PutMapping("{idEmpresa}/{idGrupoFactura}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CX_XP_MO')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("idGrupoFactura") UUID idGrupoFactura,
                              @Valid @RequestBody RequestPagoDto request) {
        return xcPagoService.update(idDataService.getIdData(), idGrupoFactura, idEmpresa, request);
    }


    @DeleteMapping("{idGrupoFactura}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CX_XP_EL')")
    public void delete(@PathVariable("idGrupoFactura") UUID idGrupoFactura) {
        xcPagoService.delete(idGrupoFactura);
    }

    @GetMapping("{idGrupoFactura}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CX_XP_VR')")
    public List<ResponseXcPagosDto> findById(@PathVariable("idGrupoFactura") UUID idGrupoFactura) {
        return xcPagoService.getFindByIdFactura(idGrupoFactura);
    }

}
