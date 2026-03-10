package com.calero.lili.api.tablas.tbSustentos;

import org.springframework.stereotype.Component;

@Component
public class TbSustentosBuilder {

    public TbSustentosEntity builderEntity(TbSustentosCreationRequestDto model) {
        return TbSustentosEntity.builder()
                .codigoSustento(model.getCodigoSustento())
                .sustento(model.getSustento())
                .build();
    }

    public TbSustentosEntity builderUpdateEntity(TbSustentosCreationRequestDto model, TbSustentosEntity item) {
        return TbSustentosEntity.builder()
                .codigoSustento(item.getCodigoSustento())
                .sustento(model.getSustento())
                .build();
    }

    public TbSustentosGetOneDto builderResponse(TbSustentosEntity model) {
        return TbSustentosGetOneDto.builder()
                .codigoSustento(model.getCodigoSustento())
                .sustento(model.getSustento())
                .build();
    }

    public TbSustentosGetListDto builderListResponse(TbSustentosEntity model) {
        return TbSustentosGetListDto.builder()
                .codigoSustento(model.getCodigoSustento())
                .sustento(model.getSustento())
                .build();
    }

}
