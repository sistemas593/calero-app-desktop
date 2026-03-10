package com.calero.lili.api.modContabilidad.modCentroCostos.builder;

import com.calero.lili.api.modContabilidad.modCentroCostos.CnCentroCostosEntity;
import com.calero.lili.api.modContabilidad.modCentroCostos.dto.CentroCostosDtoRequest;
import com.calero.lili.api.modContabilidad.modCentroCostos.dto.CentroCostosDtoResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CentroCostosBuilder {

    public CnCentroCostosEntity builderEntity(Long idData, Long idEmpresa, CentroCostosDtoRequest model) {
        return CnCentroCostosEntity.builder()
                .idCentroCostos(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .centroCostos(model.getCentroCostos())
                .codigoCentroCostos(model.getCodigoCentroCostos())
                .codigoCentroCostosOriginal(model.getCodigoCentroCostosOriginal())
                .idCodigoCentroCostosPadre(model.getIdCodigoCentroCostosPadre())
                .mayor(model.getMayor())
                .build();
    }

    public CnCentroCostosEntity builderUpdateEntity(CentroCostosDtoRequest model, CnCentroCostosEntity item) {
        return CnCentroCostosEntity.builder()
                .idCentroCostos(item.getIdCentroCostos())
                .idData(item.getIdData())
                .idEmpresa(item.getIdEmpresa())
                .centroCostos(model.getCentroCostos())
                .codigoCentroCostos(model.getCodigoCentroCostos())
                .codigoCentroCostosOriginal(model.getCodigoCentroCostosOriginal())
                .idCodigoCentroCostosPadre(model.getIdCodigoCentroCostosPadre())
                .mayor(model.getMayor())
                .build();
    }

    public CentroCostosDtoResponse builderResponse(CnCentroCostosEntity model) {
        return CentroCostosDtoResponse.builder()
                .idCentroCostos(model.getIdCentroCostos())
                .centroCostos(model.getCentroCostos())
                .codigoCentroCostos(model.getCodigoCentroCostos())
                .codigoCentroCostosOriginal(model.getCodigoCentroCostosOriginal())
                .idCodigoCentroCostosPadre(model.getIdCodigoCentroCostosPadre())
                .mayor(model.getMayor())
                .nivel(model.getNivel())
                .build();
    }


}
