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
                .tarifaGeneral(model.getTarifaGeneral())
                .tarifaReducida(model.getTarifaReducida())
                .tarifaConstruccion(model.getTarifaConstruccion())
                .fechaDesde(Objects.nonNull(model.getFechaDesde())
                        ? DateUtils.toLocalDate(model.getFechaDesde())
                        : null)
                .build();
    }

    public AdIvaPorcentajesEntity builderUpdateEntity(AdIvaPorcentajesDto model, AdIvaPorcentajesEntity item) {
        return AdIvaPorcentajesEntity.builder()
                .idIvaPorcentaje(item.getIdIvaPorcentaje())
                .tarifaGeneral(model.getTarifaGeneral())
                .tarifaReducida(model.getTarifaReducida())
                .tarifaConstruccion(model.getTarifaConstruccion())
                .fechaDesde(Objects.nonNull(model.getFechaDesde())
                        ? DateUtils.toLocalDate(model.getFechaDesde())
                        : null)
                .build();
    }


    public AdIvaPorcentajesResponseDto builderResponse(AdIvaPorcentajesEntity model) {
        return AdIvaPorcentajesResponseDto.builder()
                .iva1(model.getTarifaGeneral())
                .iva2(model.getTarifaReducida())
                .iva3(model.getTarifaConstruccion())
                .fechaDesde(Objects.nonNull(model.getFechaDesde())
                        ? DateUtils.toString(model.getFechaDesde())
                        : null)
                .build();
    }

}
