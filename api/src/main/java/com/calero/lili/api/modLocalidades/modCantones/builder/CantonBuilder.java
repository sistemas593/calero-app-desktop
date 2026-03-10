package com.calero.lili.api.modLocalidades.modCantones.builder;

import com.calero.lili.api.modLocalidades.modCantones.CantonEntity;
import com.calero.lili.api.modLocalidades.modCantones.dto.RequestCantonDto;
import com.calero.lili.api.modLocalidades.modCantones.dto.ResponseCantonDto;
import com.calero.lili.api.modLocalidades.modCantones.dto.ResponseProvinciaDto;
import com.calero.lili.api.modLocalidades.modProvincias.ProvinciaEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CantonBuilder {

    public CantonEntity builderEntity(RequestCantonDto model) {
        return CantonEntity.builder()
                .codigoCanton(model.getCodigoCanton())
                .canton(model.getCanton())
                .provincia(builderProvincia(model.getCodigoProvincia()))
                .build();
    }


    public CantonEntity builderUpdateEntity(RequestCantonDto model, CantonEntity item) {
        return CantonEntity.builder()
                .codigoCanton(model.getCodigoCanton())
                .canton(model.getCanton())
                .provincia(Objects.nonNull(model.getCodigoProvincia())
                        ? builderProvincia(model.getCodigoProvincia())
                        : item.getProvincia())
                .build();
    }

    public ResponseCantonDto builderResponse(CantonEntity model) {
        return ResponseCantonDto.builder()
                .codigoCanton(model.getCodigoCanton())
                .canton(model.getCanton())
                .provincia(builderResponseProvincia(model.getProvincia()))
                .build();
    }

    private ProvinciaEntity builderProvincia(String codigoProvincia) {
        return ProvinciaEntity.builder()
                .codigoProvincia(codigoProvincia)
                .build();
    }

    private ResponseProvinciaDto builderResponseProvincia(ProvinciaEntity model) {
        return ResponseProvinciaDto.builder()
                .codigoProvincia(model.getCodigoProvincia())
                .provincia(model.getProvincia())
                .build();
    }
}
