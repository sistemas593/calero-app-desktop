package com.calero.lili.core.modVentas.builder;

import com.calero.lili.core.modVentas.dto.DetallesErrorVentasDto;
import org.springframework.stereotype.Component;

@Component
public class DetallesVentasErrorBuilder {

    public DetallesErrorVentasDto builder(String message) {
        return DetallesErrorVentasDto.builder()
                .detalle(message)
                .build();
    }
}
