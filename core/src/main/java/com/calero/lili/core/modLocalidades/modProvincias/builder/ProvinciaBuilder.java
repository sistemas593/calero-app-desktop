package com.calero.lili.core.modLocalidades.modProvincias.builder;

import com.calero.lili.core.modLocalidades.modCantones.CantonEntity;
import com.calero.lili.core.modLocalidades.modProvincias.ProvinciaEntity;
import com.calero.lili.core.modLocalidades.modProvincias.dto.RequestProvinciaDto;
import com.calero.lili.core.modLocalidades.modProvincias.dto.ResponseProvinciaCantonDto;
import com.calero.lili.core.modLocalidades.modProvincias.dto.ResponseProvinciaListDto;
import com.calero.lili.core.modLocalidades.modProvincias.dto.ResponseProvinciaOneDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public ResponseProvinciaListDto builderListResponse(ProvinciaEntity model) {
        return ResponseProvinciaListDto.builder()
                .codigoProvincia(model.getCodigoProvincia())
                .provincia(model.getProvincia())
                .build();
    }

    public ResponseProvinciaOneDto builderResponse(ProvinciaEntity model) {
        return ResponseProvinciaOneDto.builder()
                .codigoProvincia(model.getCodigoProvincia())
                .provincia(model.getProvincia())
                .cantones(Objects.nonNull(model.getCantones())
                        ? builderListResponseCantones(model.getCantones())
                        : new ArrayList<>())
                .build();
    }

    private List<ResponseProvinciaCantonDto> builderListResponseCantones(List<CantonEntity> list) {
        return list
                .stream()
                .map(this::builderResponseCantones)
                .toList();
    }

    private ResponseProvinciaCantonDto builderResponseCantones(CantonEntity model) {
        return ResponseProvinciaCantonDto.builder()
                .codigoCanton(model.getCodigoCanton())
                .canton(model.getCanton())
                .build();
    }
}
