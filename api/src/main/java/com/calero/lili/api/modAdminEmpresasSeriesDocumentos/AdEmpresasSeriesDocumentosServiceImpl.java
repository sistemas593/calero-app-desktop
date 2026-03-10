package com.calero.lili.api.modAdminEmpresasSeriesDocumentos;

import com.calero.lili.api.modAdminEmpresasSeriesDocumentos.builder.AdEmpresasSeriesDocumentosBuilder;
import com.calero.lili.api.modAdminEmpresasSeriesDocumentos.dto.AdEmpresaSerieDocumentosGetOneDto;
import com.calero.lili.api.utils.IdDataServiceImpl;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class AdEmpresasSeriesDocumentosServiceImpl {

    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesSecuenciasRepository;
    private final AdEmpresasSeriesDocumentosBuilder adEmpresasSeriesDocumentosBuilder;
    private final IdDataServiceImpl idDataService;

    public AdEmpresaSerieDocumentosGetOneDto findBySerieAndDocumento(Long idData, Long idEmpresa, String serie, String documento) {


        return adEmpresasSeriesDocumentosBuilder.builderResponse(adEmpresasSeriesSecuenciasRepository
                .findBySerieAndDocumento(idData, idEmpresa, serie, documento)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("Serie {0}, documento {1} no exists", serie, documento))));

    }
}
