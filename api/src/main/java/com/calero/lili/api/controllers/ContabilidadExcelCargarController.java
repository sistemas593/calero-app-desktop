package com.calero.lili.api.controllers;

import com.calero.lili.core.modContabilidad.modAsientos.dto.FilterListDto;
import com.calero.lili.core.modContabilidad.services.ExcelCargaPlanCuentasServiceImpl;
import com.calero.lili.core.modContabilidad.services.ExcelCargarAsientosServiceImpl;
import com.calero.lili.api.utils.IdDataServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/cargar-excel")
@CrossOrigin(originPatterns = "*")

public class ContabilidadExcelCargarController {

    private final ExcelCargaPlanCuentasServiceImpl excelCargaPlanCuentasService;
    private final ExcelCargarAsientosServiceImpl excelCargarAsientosService;

    private final IdDataServiceImpl idDataService;
    private final AuditorAware<String> auditorAware;

    @PostMapping("/plan-cuentas/{idEmpresa}")
    @PreAuthorize("hasAuthority('CN_PC_IMEX')")
    public void uploadFilePlanCuentas(@RequestBody MultipartFile file, @PathVariable("idEmpresa") Long idEmpresa) {
        try {
            excelCargaPlanCuentasService.cargarPlanDeCuentas(idDataService.getIdData(), file, idEmpresa, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/asientos/{idEmpresa}")
    @PreAuthorize("hasAuthority('CN_PC_IMEX')")
    public void uploadFileAsientos(@RequestBody MultipartFile file,
                                   @PathVariable("idEmpresa") Long idEmpresa,
                                   FilterListDto request) {
        try {
            excelCargarAsientosService.cargarAsientos(idDataService.getIdData(), idEmpresa, file, request, auditorAware.getCurrentAuditor().orElse("SYSTEM"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
