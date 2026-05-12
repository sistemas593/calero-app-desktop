package com.calero.lili.core.modLocalidades.modProvincias.builder;

import com.calero.lili.core.modLocalidades.modProvincias.ProvinciaEntity;
import com.calero.lili.core.modLocalidades.modProvincias.dto.RequestProvinciaDto;
import com.calero.lili.core.modLocalidades.modProvincias.dto.ResponseProvinciaDto;
import org.springframework.stereotype.Component;

@Component
public class ProvinciaBuilder {

    public ProvinciaEntity builderEntity(RequestProvinciaDto model) {
        return ProvinciaEntity.builder()
                .codigoProvincia(model.getCodigoProvincia())
                .provincia(model.getProvincia())
                .build();
    }


    public ProvinciaEntity builderUpdateEntity(RequestProvinciaDto model, ProvinciaEntity item) {
        return ProvinciaEntity.builder()
                .codigoProvincia(model.getCodigoProvincia())
                .provincia(model.getProvincia())
                .build();
    }


    public ResponseProvinciaDto builderResponse(ProvinciaEntity model) {
        return ResponseProvinciaDto.builder()
                .codigoProvincia(model.getCodigoProvincia())
                .provincia(model.getProvincia())
                .build();
    }
}
