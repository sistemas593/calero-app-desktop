package com.calero.lili.core.modAdminPorcentajes.builder;

import com.calero.lili.core.modAdminPorcentajes.AdIvaPorcentajesEntity;
import com.calero.lili.core.modAdminPorcentajes.dto.AdIvaPorcentajesDto;
import com.calero.lili.core.modAdminPorcentajes.dto.AdIvaPorcentajesResponseDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AdIvaPorcentajeBuilder {

    public AdIvaPorcentajesEntity builderEntity(AdIvaPorcentajesDto model) {
        return AdIvaPorcentajesEntity.builder()
                .iva1(model.getIva1())
                .iva2(model.getIva2())
                .iva3(model.getIva3())
                .fechaDesde(Objects.nonNull(model.getFechaDesde())
                        ? DateUtils.toLocalDate(model.getFechaDesde())
                        : null)
                .build();
    }

    public AdIvaPorcentajesEntity builderUpdateEntity(AdIvaPorcentajesDto model, AdIvaPorcentajesEntity item) {
        return AdIvaPorcentajesEntity.builder()
                .idIvaPorcentaje(item.getIdIvaPorcentaje())
                .iva1(model.getIva1())
                .iva2(model.getIva2())
                .iva3(model.getIva3())
                .fechaDesde(Objects.nonNull(model.getFechaDesde())
                        ? DateUtils.toLocalDate(model.getFechaDesde())
                        : null)
                .build();
    }


    public AdIvaPorcentajesResponseDto builderResponse(AdIvaPorcentajesEntity model) {
        return AdIvaPorcentajesResponseDto.builder()
                .iva1(model.getIva1())
                .iva2(model.getIva2())
                .iva3(model.getIva3())
                .fechaDesde(Objects.nonNull(model.getFechaDesde())
                        ? DateUtils.toString(model.getFechaDesde())
                        : null)
                .build();
    }

}
