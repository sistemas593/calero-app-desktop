package com.calero.lili.core.modAdminEmpresasSeriesDocumentos;

import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.builder.AdEmpresasSeriesDocumentosBuilder;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.dto.AdEmpresaSerieDocumentosGetOneDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class AdEmpresasSeriesDocumentosServiceImpl {

    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesSecuenciasRepository;
    private final AdEmpresasSeriesDocumentosBuilder adEmpresasSeriesDocumentosBuilder;


    public AdEmpresaSerieDocumentosGetOneDto findBySerieAndDocumento(Long idData, Long idEmpresa, String serie, String documento) {


        return adEmpresasSeriesDocumentosBuilder.builderResponse(adEmpresasSeriesSecuenciasRepository
                .findBySerieAndDocumento(idData, idEmpresa, serie, documento)
                .orElseThrow(() -> new GeneralException(MessageFormat
                        .format("Serie {0}, documento {1} no exists", serie, documento))));

    }
}
