package com.calero.lili.core.tablas.tbRetencionesCodigos;

import org.springframework.stereotype.Component;

@Component
public class TbRetencionesCodigosBuilder {

    public TbRetencionesCodigosEntity builderEntity(TbRetencionesCodigosCreationRequestDto model) {
        return TbRetencionesCodigosEntity.builder()
                .codigoRetencion(model.getCodigoRetencion())
                .nombreRetencion(model.getNombreRetencion())
                .codigo(model.getCodigo())
                .vigenteHasta(model.getVigenteHasta())
                .vigenteDesde(model.getVigenteDesde())
                .porcentaje(model.getPorcentaje())
                .build();
    }


    public TbRetencionesCodigosEntity builderUpdateEntity(TbRetencionesCodigosCreationRequestDto model,
                                                          TbRetencionesCodigosEntity item) {
        return TbRetencionesCodigosEntity.builder()
                .codigoRetencion(item.getCodigoRetencion())
                .nombreRetencion(model.getNombreRetencion())
                .codigo(model.getCodigo())
                .vigenteHasta(model.getVigenteHasta())
                .vigenteDesde(model.getVigenteDesde())
                .porcentaje(model.getPorcentaje())
                .build();
    }


    public TbRetencionesCodigosGetOneDto builderResponse(TbRetencionesCodigosEntity model) {
        return TbRetencionesCodigosGetOneDto.builder()
                .codigoRetencion(model.getCodigoRetencion())
                .nombreRetencion(model.getNombreRetencion())
                .codigo(model.getCodigo())
                .vigenteHasta(model.getVigenteHasta())
                .vigenteDesde(model.getVigenteDesde())
                .porcentaje(model.getPorcentaje())
                .build();
    }


    public TbRetencionesCodigosGetListDto builderListResponse(TbRetencionesCodigosEntity model) {
        return TbRetencionesCodigosGetListDto.builder()
                .codigoRetencion(model.getCodigoRetencion())
                .nombreRetencion(model.getNombreRetencion())
                .codigo(model.getCodigo())
                .vigenteHasta(model.getVigenteHasta())
                .vigenteDesde(model.getVigenteDesde())
                .porcentaje(model.getPorcentaje())
                .build();
    }

}
