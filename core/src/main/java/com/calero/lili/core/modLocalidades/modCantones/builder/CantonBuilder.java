package com.calero.lili.core.modLocalidades.modCantones.builder;

import com.calero.lili.core.modLocalidades.modCantones.CantonEntity;
import com.calero.lili.core.modLocalidades.modCantones.dto.RequestCantonDto;
import com.calero.lili.core.modLocalidades.modCantones.dto.ResponseCantonDto;
import com.calero.lili.core.modLocalidades.modCantones.dto.ResponseCantonListDto;
import com.calero.lili.core.modLocalidades.modProvincias.ProvinciaEntity;
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
                .codigoCanton(item.getCodigoCanton())
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
                .codigoProvincia(model.getProvincia().getCodigoProvincia())
                .provincia(model.getProvincia().getProvincia())
                .build();
    }

    public ResponseCantonListDto builderListResponse(CantonEntity model) {
        return ResponseCantonListDto.builder()
                .codigoCanton(model.getCodigoCanton())
                .canton(model.getCanton())
                .build();
    }

    private ProvinciaEntity builderProvincia(String codigoProvincia) {
        return ProvinciaEntity.builder()
                .codigoProvincia(codigoProvincia)
                .build();
    }
}
