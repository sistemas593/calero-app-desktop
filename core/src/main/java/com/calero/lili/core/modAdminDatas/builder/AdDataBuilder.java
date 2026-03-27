package com.calero.lili.core.modAdminDatas.builder;

import com.calero.lili.core.modAdminDatas.AdDataEntity;
import com.calero.lili.core.modAdminDatas.dto.AdDataResponseConfiguracionDto;
import com.calero.lili.core.modAdminDatas.dto.AdDatasCreationRequestDto;
import com.calero.lili.core.modAdminDatas.dto.AdDatasDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class AdDataBuilder {

    public AdDataEntity builderEntity(AdDatasCreationRequestDto model) {
        return AdDataEntity.builder()
                .siguienteIdEmpresa(1L)
                .fechaCreacion(LocalDate.from(LocalDateTime.now()))
                .data(model.getData())
                .idConfiguracion(model.getIdConfiguracion())
                .build();
    }

    public AdDataEntity builderUpdateEntity(AdDatasCreationRequestDto model, AdDataEntity entidad) {
        return AdDataEntity.builder()
                .idData(entidad.getIdData())
                .siguienteIdEmpresa(1L)
                .fechaCreacion(LocalDate.from(LocalDateTime.now()))
                .data(model.getData())
                .idConfiguracion(model.getIdConfiguracion())
                .build();
    }

    public AdDatasDto builderResponse(AdDataEntity entity) {
        return AdDatasDto
                .builder()
                .idData(entity.getIdData())
                .data(entity.getData())
                .build();
    }


    public AdDataResponseConfiguracionDto builderResponseConfiguracion(AdDataEntity entity){
        return AdDataResponseConfiguracionDto.builder()
                .idData(entity.getIdData())
                .idConfiguracion(entity.getIdConfiguracion())
                .build();
    }

    public AdDataEntity builderAdDataCreate(AdDataEntity model, Long nextIdData) {
        return AdDataEntity.builder()
                .idData(model.getIdData())
                .data(model.getData())
                .fechaCreacion(LocalDate.now())
                .siguienteIdEmpresa(nextIdData + 1)
                .build();
    }
}
