package com.calero.lili.api.controllers;

import com.calero.lili.api.modAuditoria.AuditorAwareImpl;
import com.calero.lili.core.comprobantes.services.DeRecibidasServiceImpl;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/recibidos")
@CrossOrigin(originPatterns = "*")

public class ImpuestosRecibidosController {

    private final DeRecibidasServiceImpl deRecibidasService;
    private final IdDataServiceImpl idDataService;
    private final AuditorAwareImpl auditorAware;


    @PostMapping("files/{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    public CpImpuestosRecibirListCreationResponseDto createFiles(@PathVariable("idEmpresa") Long idEmpresa,
                                                                 @RequestBody List<MultipartFile> documentos) {
        return deRecibidasService.createFiles(idDataService.getIdData(), idEmpresa, documentos,
                auditorAware.getCurrentAuditor().orElse("SYSTEM"));
    }

}
