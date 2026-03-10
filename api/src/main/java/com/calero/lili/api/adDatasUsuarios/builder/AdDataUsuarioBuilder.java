package com.calero.lili.api.adDatasUsuarios.builder;

import com.calero.lili.api.adDatasUsuarios.AdDataUsuarioEntity;
import com.calero.lili.api.adDatasUsuarios.dto.AdDataUsuarioCreationRequestDto;
import com.calero.lili.api.adDatasUsuarios.dto.AdDataUsuarioCreationResponseDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AdDataUsuarioBuilder {

    public AdDataUsuarioEntity builderEntity(AdDataUsuarioCreationRequestDto model, Long idData) {
        return AdDataUsuarioEntity.builder()
                .idRegistro(UUID.randomUUID())
                .idData(idData)
                .idUsuario(model.getIdUsuario())
                .build();
    }

    public AdDataUsuarioEntity builderUpdateEntity(AdDataUsuarioCreationRequestDto model, AdDataUsuarioEntity item) {
        return AdDataUsuarioEntity.builder()
                .idRegistro(item.getIdRegistro())
                .idData(item.getIdData())
                .idUsuario(model.getIdUsuario())
                .build();
    }

    public AdDataUsuarioCreationResponseDto builderResponse(AdDataUsuarioEntity model) {
        return AdDataUsuarioCreationResponseDto.builder()
                .idRegistro(model.getIdRegistro())
                .idData(model.getIdData())
                .idUsuario(model.getIdUsuario())
                .build();
    }
}
