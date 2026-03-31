package com.calero.lili.core.modComprasItemsImpuesto.builder;

import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosEntity;
import com.calero.lili.core.modComprasItemsImpuesto.dto.GeImpuestoRequestDto;
import com.calero.lili.core.modComprasItemsImpuesto.dto.GeImpuestoResponseDto;
import org.springframework.stereotype.Component;

@Component
public class GeImpuestosBuilder {


    public GeImpuestosEntity builderEntity(GeImpuestoRequestDto model) {
        return GeImpuestosEntity.builder()
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .build();
    }


    public GeImpuestosEntity builderUpdateEntity(GeImpuestosEntity entidad, GeImpuestoRequestDto model) {
        return GeImpuestosEntity.builder()
                .idImpuesto(entidad.getIdImpuesto())
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .build();
    }

    public GeImpuestoResponseDto builderResponse(GeImpuestosEntity model) {
        return GeImpuestoResponseDto.builder()
                .idImpuesto(model.getIdImpuesto())
                .codigo(model.getCodigo())
                .codigoPorcentaje(model.getCodigoPorcentaje())
                .tarifa(model.getTarifa())
                .build();
    }

}
