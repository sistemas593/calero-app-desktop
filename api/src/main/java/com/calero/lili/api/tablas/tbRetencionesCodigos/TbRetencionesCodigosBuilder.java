package com.calero.lili.api.tablas.tbRetencionesCodigos;

import org.springframework.stereotype.Component;

@Component
public class TbRetencionesCodigosBuilder {

    public TbRetencionesCodigosEntity builderEntity(TbRetencionesCodigosCreationRequestDto model) {
        return TbRetencionesCodigosEntity.builder()
                .codigoRetencion(model.getCodigoRetencion())
                .nombreRetencion(model.getNombreRetencion())
                .build();
    }


    public TbRetencionesCodigosEntity builderUpdateEntity(TbRetencionesCodigosCreationRequestDto model,
                                                          TbRetencionesCodigosEntity item) {
        return TbRetencionesCodigosEntity.builder()
                .codigoRetencion(item.getCodigoRetencion())
                .nombreRetencion(model.getNombreRetencion())
                .build();
    }


    public TbRetencionesCodigosGetOneDto builderResponse(TbRetencionesCodigosEntity model) {
        return TbRetencionesCodigosGetOneDto.builder()
                .codigoRetencion(model.getCodigoRetencion())
                .nombreRetencion(model.getNombreRetencion())
                .build();
    }


    public TbRetencionesCodigosGetListDto builderListResponse(TbRetencionesCodigosEntity model) {
        return TbRetencionesCodigosGetListDto.builder()
                .codigoRetencion(model.getCodigoRetencion())
                .nombreRetencion(model.getNombreRetencion())
                .build();
    }

}
