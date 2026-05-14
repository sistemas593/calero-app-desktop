package com.calero.lili.core.modAdModulos.builder;

import com.calero.lili.core.modAdModulos.AdModulosEntity;
import com.calero.lili.core.modAdModulos.dto.AdModuloRequestDto;
import com.calero.lili.core.modAdModulos.dto.AdModuloResponseDto;
import org.springframework.stereotype.Component;

@Component
public class AdModuloBuilder {

    public AdModulosEntity builderEntity(AdModuloRequestDto model) {
        return AdModulosEntity.builder()
                .modulo(model.getModulo())
                .build();
    }

    public AdModulosEntity builderUpdateEntity(AdModuloRequestDto model, AdModulosEntity item) {
        return AdModulosEntity.builder()
                .idModulo(item.getIdModulo())
                .modulo(model.getModulo())
                .build();
    }

    public AdModuloResponseDto builderResponse(AdModulosEntity model) {
        return AdModuloResponseDto.builder()
                .idModuloData(model.getIdModulo())
                .modulo(model.getModulo())
                .build();
    }
}
