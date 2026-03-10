package com.calero.lili.api.modAdminPorcentajes.builder;

import com.calero.lili.api.modAdminPorcentajes.AdIvaPorcentajesEntity;
import com.calero.lili.api.modAdminPorcentajes.dto.AdIvaPorcentajesDto;
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
                .fechaDesde(Objects.nonNull(model.getFechaDesde()) ? DateUtils.toLocalDate(model.getFechaDesde()) : null)
                .build();
    }

}
