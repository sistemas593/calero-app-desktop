package com.calero.lili.api.tablas.tbDocumentos;

import org.springframework.stereotype.Component;

@Component
public class TbDocumentoBuilder {

    public TbDocumentoEntity builderEntity(TbDocumentosCreationRequestDto model) {
        return TbDocumentoEntity.builder()
                .codigoDocumento(model.getCodigoDocumento())
                .documento(model.getDocumento())
                .build();
    }

    public TbDocumentoEntity builderUpdateEntity(TbDocumentosCreationRequestDto model, TbDocumentoEntity item) {
        return TbDocumentoEntity.builder()
                .codigoDocumento(item.getCodigoDocumento())
                .documento(model.getDocumento())
                .build();
    }


    public TbDocumentosGetOneDto builderResponse(TbDocumentoEntity model) {
        return TbDocumentosGetOneDto.builder()
                .codigoDocumento(model.getCodigoDocumento())
                .documento(model.getDocumento())
                .build();
    }


    public TbDocumentosGetListDto builderListResponse(TbDocumentoEntity model) {
        return TbDocumentosGetListDto.builder()
                .codigoDocumento(model.getCodigoDocumento())
                .documento(model.getDocumento())
                .build();
    }

}
