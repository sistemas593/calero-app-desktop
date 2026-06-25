package com.calero.lili.core.adConfiguracion.builder;

import com.calero.lili.core.adConfiguracion.dto.AdMailEnviadosTotalResponseDto;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailEnviadosTotalEntity;
import org.springframework.stereotype.Component;

@Component
public class AdMailBuilderTotalBuilder {

    public AdMailEnviadosTotalResponseDto builderResponse(AdMailEnviadosTotalEntity model) {
        return AdMailEnviadosTotalResponseDto.builder()
                .id(model.getId())
                .total(model.getTotal())
                .clave1(model.getClave1())
                .periodo(model.getPeriodo())
                .build();
    }
}
