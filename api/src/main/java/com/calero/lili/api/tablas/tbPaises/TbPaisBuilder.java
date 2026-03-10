package com.calero.lili.api.tablas.tbPaises;

import org.springframework.stereotype.Component;

@Component
public class TbPaisBuilder {

    public TbPaisEntity builderEntity(TbPaisCreationRequestDto model) {
        return TbPaisEntity.builder()
                .codigoPais(model.getCodigoPais())
                .pais(model.getPais())
                .build();
    }

    public TbPaisEntity builderUpdateEntity(TbPaisCreationRequestDto model, TbPaisEntity item) {
        return TbPaisEntity.builder()
                .codigoPais(item.getCodigoPais())
                .pais(model.getPais())
                .build();
    }

    public TbPaisGetOneDto builderResponse(TbPaisEntity model) {
        return TbPaisGetOneDto.builder()
                .codigoPais(model.getCodigoPais())
                .pais(model.getPais())
                .build();
    }

    public TbPaisGetListDto builderResponseList(TbPaisEntity model) {
        return TbPaisGetListDto.builder()
                .codigoPais(model.getCodigoPais())
                .pais(model.getPais())
                .build();
    }


}
