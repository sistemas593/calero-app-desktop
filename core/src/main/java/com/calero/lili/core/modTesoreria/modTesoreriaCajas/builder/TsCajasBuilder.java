package com.calero.lili.core.modTesoreria.modTesoreriaCajas.builder;

import com.calero.lili.core.modTesoreria.modTesoreriaCajas.TsCajasEntity;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.dto.TsCajasRequestDto;
import com.calero.lili.core.modTesoreria.modTesoreriaCajas.dto.TsCajasResponseDto;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class TsCajasBuilder {


    public TsCajasEntity builderEntity(Long idData, Long idEmpresa, TsCajasRequestDto model) {
        return TsCajasEntity.builder()
                .idCaja(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .nombre(model.getNombre())
                .codigoCaja(model.getCodigoCaja())
                .build();
    }


    public TsCajasEntity builderUpdateEntity(TsCajasRequestDto model, TsCajasEntity entidad) {
        return TsCajasEntity.builder()
                .idCaja(entidad.getIdCaja())
                .idData(entidad.getIdData())
                .idEmpresa(entidad.getIdEmpresa())
                .nombre(model.getNombre())
                .codigoCaja(model.getCodigoCaja())
                .build();
    }


    public TsCajasResponseDto builderResponse(TsCajasEntity model) {
        return TsCajasResponseDto.builder()
                .idCaja(model.getIdCaja())
                .nombre(model.getNombre())
                .codigoCaja(model.getCodigoCaja())
                .idEntidad(Objects.nonNull(model.getEntidad()) ? model.getEntidad().getIdEntidad() : null)
                .entidad(Objects.nonNull(model.getEntidad()) ? model.getEntidad().getEntidad() : null)
                .build();
    }
}
