package com.calero.lili.api.modAdminEmpresasSeriesDocumentos.builder;

import com.calero.lili.api.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosEntity;
import com.calero.lili.api.modAdminEmpresasSeriesDocumentos.dto.AdEmpresaSerieDocumentosGetListDto;
import com.calero.lili.api.modAdminEmpresasSeriesDocumentos.dto.AdEmpresaSerieDocumentosGetOneDto;
import org.springframework.stereotype.Component;

@Component
public class AdEmpresasSeriesDocumentosBuilder {


    public AdEmpresaSerieDocumentosGetOneDto builderResponse(AdEmpresasSeriesDocumentosEntity model) {
        return AdEmpresaSerieDocumentosGetOneDto.builder()
                .secuencial(model.getSecuencial())
                .build();
    }


    public AdEmpresaSerieDocumentosGetListDto builderListResponse(AdEmpresasSeriesDocumentosEntity model) {
        return AdEmpresaSerieDocumentosGetListDto.builder()
                .idDocumento(model.getIdDocumento())
                .documento(model.getDocumento())
                .numeroAutorizacion(model.getNumeroAutorizacion())
                .secuencial(model.getSecuencial())
                .build();
    }

}
