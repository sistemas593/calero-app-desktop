package com.calero.lili.api.modContabilidad.modPlanCuentas;

import com.calero.lili.core.dtos.ResponseDto;
import com.calero.lili.api.modContabilidad.modPlanCuentas.dto.CnPlanCuentaCreationRequestDto;
import com.calero.lili.api.modContabilidad.modPlanCuentas.dto.CnPlanCuentaGetListDto;
import com.calero.lili.api.modContabilidad.modPlanCuentas.dto.CnPlanCuentaGetOneDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
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
@RequestMapping("api/v1.0/plan-cuentas")
@CrossOrigin(originPatterns = "*")

public class CnPlanCuentasController {

    private final CnPlanCuentasServiceImpl cnPlanCuentasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CN_PC_CR')")
    public ResponseDto create(@PathVariable("idEmpresa") Long idEmpresa,
                              @RequestBody @Valid CnPlanCuentaCreationRequestDto request) {
        return cnPlanCuentasService.create(idDataService.getIdData(), idEmpresa, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @PutMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_PC_MO')")
    public ResponseDto update(@PathVariable("idEmpresa") Long idEmpresa,
                              @PathVariable("id") UUID id,
                              @RequestBody @Valid CnPlanCuentaCreationRequestDto request) {
        return cnPlanCuentasService.update(idDataService.getIdData(), idEmpresa, id, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @DeleteMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('CN_PC_EL')")
    public void delete(@PathVariable("idEmpresa") Long idEmpresa,
                       @PathVariable("id") UUID id) {
        cnPlanCuentasService.delete(idDataService.getIdData(), idEmpresa, id, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

    @GetMapping("{idEmpresa}/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_PC_VR')")
    public CnPlanCuentaGetOneDto findById(@PathVariable("idEmpresa") Long idEmpresa,
                                          @PathVariable("id") UUID id) {
        return cnPlanCuentasService.findById(idDataService.getIdData(), idEmpresa, id);
    }

    @GetMapping("listar/{idEmpresa}")
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasAuthority('CN_PC_VR')")
    public List<CnPlanCuentaGetListDto> findAll(@PathVariable("idEmpresa") Long idEmpresa) {
        return cnPlanCuentasService.findAll(idDataService.getIdData(), idEmpresa);
    }


}
