package com.calero.lili.api.modContabilidad.modEnlances.builder;

import com.calero.lili.api.modContabilidad.modEnlances.CnEnlacesGeneralesEntity;
import com.calero.lili.api.modContabilidad.modEnlances.dto.CnEnlaceRequestDto;
import com.calero.lili.api.modContabilidad.modEnlances.dto.CnEnlaceResponseDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CnEnlaceBuilder {

    public CnEnlacesGeneralesEntity builderEntity(CnEnlaceRequestDto model) {
        return CnEnlacesGeneralesEntity.builder()
                .idEnlace(UUID.randomUUID())
                .idCuenta(model.getIdCuenta())
                .detalle(model.getDetalle())
                .codigo(model.getCodigo())
                .build();
    }


    public CnEnlacesGeneralesEntity builderUpdate(CnEnlaceRequestDto model, CnEnlacesGeneralesEntity item) {
        return CnEnlacesGeneralesEntity.builder()
                .idEnlace(item.getIdEnlace())
                .idCuenta(model.getIdCuenta())
                .detalle(model.getDetalle())
                .codigo(model.getCodigo())
                .build();
    }

    public CnEnlaceResponseDto builderResponse(CnEnlacesGeneralesEntity model) {
        return CnEnlaceResponseDto.builder()
                .idEnlace(model.getIdEnlace())
                .idCuenta(model.getIdCuenta())
                .detalle(model.getDetalle())
                .codigo(model.getCodigo())
                .build();
    }


}
