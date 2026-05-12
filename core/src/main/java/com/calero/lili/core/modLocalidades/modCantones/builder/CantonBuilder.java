package com.calero.lili.core.modLocalidades.modCantones.builder;

import com.calero.lili.core.modLocalidades.modCantones.CantonEntity;
import com.calero.lili.core.modLocalidades.modCantones.dto.RequestCantonDto;
import com.calero.lili.core.modLocalidades.modCantones.dto.ResponseCantonDto;
import com.calero.lili.core.modLocalidades.modCantones.dto.ResponseCantonParroquiaDto;
import com.calero.lili.core.modLocalidades.modParroquias.ParroquiaEntity;
import com.calero.lili.core.modLocalidades.modProvincias.ProvinciaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
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
                .parroquias(builderListResponseParroquia(model.getParroquias()))
                .build();
    }


    private List<ResponseCantonParroquiaDto> builderListResponseParroquia(List<ParroquiaEntity> list) {
        return list.stream()
                .map(this::builderResponseParroquia)
                .toList();
    }

    private ResponseCantonParroquiaDto builderResponseParroquia(ParroquiaEntity model) {
        return ResponseCantonParroquiaDto.builder()
                .codigoParroquia(model.getCodigoParroquia())
                .parroquia(model.getParroquia())
                .build();
    }

    private ProvinciaEntity builderProvincia(String codigoProvincia) {
        return ProvinciaEntity.builder()
                .codigoProvincia(codigoProvincia)
                .build();
    }
}
