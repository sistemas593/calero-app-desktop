package com.calero.lili.api.modAdminEmpresasSeriesDocumentos;


import com.calero.lili.api.modAdminEmpresasSeriesDocumentos.dto.AdEmpresaSerieDocumentosGetOneDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1.0/series/documentos")
@CrossOrigin(originPatterns = "*")

public class AdEmpresasSeriesDocumentosController {

    private final AdEmpresasSeriesDocumentosServiceImpl adEmpresasSeriesSecuenciasService;
    private final IdDataServiceImpl idDataService;

    @GetMapping("{idEmpresa}/{serie}/{documento}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('AD_DO_VR')")
    public AdEmpresaSerieDocumentosGetOneDto findBySerieAndDocumento(@PathVariable("idEmpresa") Long idEmpresa,
                                                                     @PathVariable("serie") String serie,
                                                                     @PathVariable("documento") String documento) {
        return adEmpresasSeriesSecuenciasService.findBySerieAndDocumento(idDataService.getIdData(), idEmpresa, serie, documento);
    }

}
