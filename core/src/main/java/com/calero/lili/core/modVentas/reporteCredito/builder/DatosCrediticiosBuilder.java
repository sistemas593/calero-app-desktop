package com.calero.lili.core.modVentas.reporteCredito.builder;

import com.calero.lili.core.modVentas.reporteCredito.DatosCrediticiosEntity;
import com.calero.lili.core.modVentas.reporteCredito.dto.DatosCrediticiosResponseDto;
import org.springframework.stereotype.Component;

@Component
public class DatosCrediticiosBuilder {

    public DatosCrediticiosResponseDto builderResponseList(DatosCrediticiosEntity entity) {
        return DatosCrediticiosResponseDto.builder()
                .idDatosCrediticios(entity.getIdDatosCrediticios())
                .periodo(entity.getPeriodo())
                .build();
    }
}
