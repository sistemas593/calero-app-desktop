package com.calero.lili.core.tablas.tbRetenciones;

import org.springframework.stereotype.Component;

@Component
public class TbRetencionesBuilder {

    public TbRetencionEntity builderEntity(TbRetencionesCreationRequestDto model){
        return TbRetencionEntity.builder()
                .codigo(model.getCodigo())
                .nombreRetencion(model.getNombreRetencion())
                .build();
    }

    public TbRetencionEntity builderUpdateEntity(TbRetencionesCreationRequestDto model, TbRetencionEntity item){
        return TbRetencionEntity.builder()
                .codigo(item.getCodigo())
                .nombreRetencion(model.getNombreRetencion())
                .build();
    }

    public TbRetencionesGetOneDto builderResponse(TbRetencionEntity model){
        return TbRetencionesGetOneDto.builder()
                .codigo(model.getCodigo())
                .nombreRetencion(model.getNombreRetencion())
                .build();
    }

    public TbRetencionesGetListDto builderListResponse(TbRetencionEntity model){
        return TbRetencionesGetListDto.builder()
                .codigo(model.getCodigo())
                .nombreRetencion(model.getNombreRetencion())
                .build();
    }


}
