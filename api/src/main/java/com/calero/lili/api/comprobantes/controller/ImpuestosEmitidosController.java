package com.calero.lili.api.comprobantes.controller;

import com.calero.lili.api.comprobantes.services.DeEmitidasServiceImpl;
import com.calero.lili.api.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
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
@RequestMapping("api/v1.0/emitidos")
@CrossOrigin(originPatterns = "*")

public class ImpuestosEmitidosController {

    private final DeEmitidasServiceImpl deEmitidasService;
    private final IdDataServiceImpl idDataService;

    // Correción no volver a guardar el tipo tercero.

    @PostMapping("files/{idEmpresa}")
    @ResponseStatus(HttpStatus.CREATED)
    public CpImpuestosRecibirListCreationResponseDto createFiles(@PathVariable("idEmpresa") Long idEmpresa,
                                                                 @RequestBody List<MultipartFile> documentos) {
        return deEmitidasService.createFiles(idDataService.getIdData(), idEmpresa, documentos);
    }

}
